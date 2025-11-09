package com.simonjamesrowe.backend.entrypoints.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.Constants;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;
import com.simonjamesrowe.backend.core.model.IndexBlogRequest;
import com.simonjamesrowe.backend.core.model.IndexSiteRequest;
import com.simonjamesrowe.backend.core.usecase.IndexBlogUseCase;
import com.simonjamesrowe.backend.core.usecase.IndexSiteUseCase;
import com.simonjamesrowe.backend.dataproviders.cms.ICmsRestApi;
import com.simonjamesrowe.backend.mapper.BlogMapper;
import com.simonjamesrowe.backend.mapper.JobMapper;
import com.simonjamesrowe.backend.mapper.SkillsGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaEventConsumer implements IKafkaEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final IndexBlogUseCase indexBlogUseCase;
    private final IndexSiteUseCase indexSiteUseCase;
    private final ObjectMapper objectMapper;
    private final ICmsRestApi cmsRestApi;

    public KafkaEventConsumer(IndexBlogUseCase indexBlogUseCase,
                             IndexSiteUseCase indexSiteUseCase,
                             ObjectMapper objectMapper,
                             ICmsRestApi cmsRestApi) {
        this.indexBlogUseCase = indexBlogUseCase;
        this.indexSiteUseCase = indexSiteUseCase;
        this.objectMapper = objectMapper;
        this.cmsRestApi = cmsRestApi;
    }

    @KafkaListener(topics = "${namespace:LOCAL}_EVENTS")
    @Override
    public void consumeEvents(List<WebhookEventDTO> events) {
        try {
            LOG.info("Received events from kafka: {}", events);
            updateBlogSearchIndex(events);
            updateSiteSearchIndex(events);
        } catch (Exception exception) {
            if (exception.getCause() != null
                    && exception.getCause().getClass().getPackageName()
                            .startsWith("com.fasterxml.jackson")) {
                LOG.error("Error with json deserialization", exception);
            } else {
                throw new RuntimeException("Failed to process events", exception);
            }
        }
    }

    private void updateSiteSearchIndex(List<WebhookEventDTO> events) {
        try {
            boolean hasSkillEvents = events.stream().anyMatch(event -> Constants.TYPE_SKILL.equals(event.model()));
            if (hasSkillEvents) {
                var skillsGroups = cmsRestApi.getAllSkillsGroups();
                skillsGroups.forEach(skillsGroup -> {
                    List<IndexSiteRequest> siteRequests = SkillsGroupMapper.toSiteIndexRequests(skillsGroup);
                    indexSiteUseCase.indexSites(siteRequests);
                });
            }

            List<IndexSiteRequest> jobSiteRequests = events.stream()
                .filter(event -> Constants.TYPE_JOB.equals(event.model()))
                .map(event -> objectMapper.convertValue(event.entry(), JobResponseDTO.class))
                .map(JobMapper::toIndexSiteRequest)
                .collect(Collectors.toList());

            if (!jobSiteRequests.isEmpty()) {
                indexSiteUseCase.indexSites(jobSiteRequests);
            }

            List<IndexSiteRequest> blogSiteRequests = events.stream()
                .filter(event -> Constants.TYPE_BLOG.equals(event.model()))
                .map(event -> objectMapper.convertValue(event.entry(), BlogResponseDTO.class))
                .map(BlogMapper::toSiteIndexRequest)
                .collect(Collectors.toList());

            if (!blogSiteRequests.isEmpty()) {
                indexSiteUseCase.indexSites(blogSiteRequests);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update site search index", e);
        }
    }

    private void updateBlogSearchIndex(List<WebhookEventDTO> events) {
        try {
            List<IndexBlogRequest> blogRequests = events.stream()
                .filter(event -> Constants.TYPE_BLOG.equals(event.model()))
                .map(event -> objectMapper.convertValue(event.entry(), BlogResponseDTO.class))
                .map(BlogMapper::toBlogIndexRequest)
                .collect(Collectors.toList());

            if (!blogRequests.isEmpty()) {
                indexBlogUseCase.indexBlogs(blogRequests);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update blog search index", e);
        }
    }
}