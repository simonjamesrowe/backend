package com.simonjamesrowe.searchservice.test.entrypoints.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase;
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase;
import com.simonjamesrowe.searchservice.dataproviders.cms.ICmsRestApi;
import com.simonjamesrowe.searchservice.entrypoints.kafka.KafkaEventConsumer;
import com.simonjamesrowe.searchservice.mapper.BlogMapper;
import com.simonjamesrowe.searchservice.mapper.JobMapper;
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

    @Mock
    private ICmsRestApi cmsRestApi;

    @Mock
    private IndexBlogUseCase indexBlogUseCase;

    @Mock
    private IndexSiteUseCase indexSiteUseCase;

    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaEventConsumer kafkaEventConsumer;

    @BeforeEach
    void beforeEach() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        kafkaEventConsumer = new KafkaEventConsumer(indexBlogUseCase, indexSiteUseCase, objectMapper, cmsRestApi);
    }

    @Test
    void consumingWebhookBlogEventsWillIndexBlogsAndSite() {
        try (MockedStatic<BlogMapper> blogMapperMock = Mockito.mockStatic(BlogMapper.class)) {
            BlogResponseDTO blog1 = new BlogResponseDTO(
                "1", true, List.of(), ZonedDateTime.now(), ZonedDateTime.now(),
                List.of(), "Short desc 1", "Blog 1", "Content 1",
                TestUtils.image("blog1", 200)
            );
            BlogResponseDTO blog2 = new BlogResponseDTO(
                "2", true, List.of(), ZonedDateTime.now(), ZonedDateTime.now(),
                List.of(), "Short desc 2", "Blog 2", "Content 2",
                TestUtils.image("blog2", 200)
            );

            JsonNode blog1Node = objectMapper.convertValue(blog1, JsonNode.class);
            JsonNode blog2Node = objectMapper.convertValue(blog2, JsonNode.class);

            WebhookEventDTO webhookEvent1 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "blog", blog1Node
            );
            WebhookEventDTO webhookEvent2 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "blog", blog2Node
            );

            IndexBlogRequest indexBlogRequest1 = new IndexBlogRequest(
                "1", "Blog 1", "Short desc 1", "Content 1", List.of(), List.of(),
                "uploads/blog1-thumb.jpg", "uploads/blog1-sml.jpg", "uploads/blog1-med.jpg",
                LocalDate.now(), true
            );
            IndexBlogRequest indexBlogRequest2 = new IndexBlogRequest(
                "2", "Blog 2", "Short desc 2", "Content 2", List.of(), List.of(),
                "uploads/blog2-thumb.jpg", "uploads/blog2-sml.jpg", "uploads/blog2-med.jpg",
                LocalDate.now(), true
            );

            IndexSiteRequest siteIndexRequest1 = new IndexSiteRequest(
                "blog_1", "/blogs/1", "Blog 1", "Blogs",
                "uploads/blog1-thumb.jpg", "Short desc 1", "Content 1"
            );
            IndexSiteRequest siteIndexRequest2 = new IndexSiteRequest(
                "blog_2", "/blogs/2", "Blog 2", "Blogs",
                "uploads/blog2-thumb.jpg", "Short desc 2", "Content 2"
            );

            blogMapperMock.when(() -> BlogMapper.toBlogIndexRequest(any(BlogResponseDTO.class)))
                .thenReturn(indexBlogRequest1, indexBlogRequest2);
            blogMapperMock.when(() -> BlogMapper.toSiteIndexRequest(any(BlogResponseDTO.class)))
                .thenReturn(siteIndexRequest1, siteIndexRequest2);

            kafkaEventConsumer.consumeEvents(List.of(webhookEvent1, webhookEvent2));

            verify(indexBlogUseCase).indexBlogs(List.of(indexBlogRequest1, indexBlogRequest2));
            verify(indexSiteUseCase).indexSites(List.of(siteIndexRequest1, siteIndexRequest2));
        }
    }

    @Test
    void consumingWebhookJobEventsWillIndexSite() {
        try (MockedStatic<JobMapper> jobMapperMock = Mockito.mockStatic(JobMapper.class)) {
            JobResponseDTO job1 = new JobResponseDTO(
                "1", "Job 1", "Company 1", "http://company1.com", "Short desc 1", "Long desc 1",
                TestUtils.image("company1", 200), ZonedDateTime.now(), ZonedDateTime.now(),
                LocalDate.now(), LocalDate.now(), true, false, "Location 1"
            );
            JobResponseDTO job2 = new JobResponseDTO(
                "2", "Job 2", "Company 2", "http://company2.com", "Short desc 2", "Long desc 2",
                TestUtils.image("company2", 200), ZonedDateTime.now(), ZonedDateTime.now(),
                LocalDate.now(), LocalDate.now(), true, false, "Location 2"
            );

            JsonNode job1Node = objectMapper.convertValue(job1, JsonNode.class);
            JsonNode job2Node = objectMapper.convertValue(job2, JsonNode.class);

            WebhookEventDTO webhookEvent1 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "job", job1Node
            );
            WebhookEventDTO webhookEvent2 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "job", job2Node
            );

            IndexSiteRequest siteIndexRequest1 = new IndexSiteRequest(
                "job_1", "/jobs/1", "Job 1 (Company 1) - 2025 - 2025", "Jobs",
                "uploads/company1-thumb.jpg", "Short desc 1", "Long desc 1"
            );
            IndexSiteRequest siteIndexRequest2 = new IndexSiteRequest(
                "job_2", "/jobs/2", "Job 2 (Company 2) - 2025 - 2025", "Jobs",
                "uploads/company2-thumb.jpg", "Short desc 2", "Long desc 2"
            );

            jobMapperMock.when(() -> JobMapper.toIndexSiteRequest(any(JobResponseDTO.class)))
                .thenReturn(siteIndexRequest1, siteIndexRequest2);

            kafkaEventConsumer.consumeEvents(List.of(webhookEvent1, webhookEvent2));

            verify(indexSiteUseCase).indexSites(List.of(siteIndexRequest1, siteIndexRequest2));
        }
    }

    @Test
    void consumingWebhookSkillEventsWillIndexSite() {
        try (MockedStatic<SkillsGroupMapper> skillsGroupMapperMock = Mockito.mockStatic(SkillsGroupMapper.class)) {
            SkillResponseDTO skill1 = new SkillResponseDTO(
                "1", "Skill 1", ZonedDateTime.now(), ZonedDateTime.now(), "Desc 1", 5.0, 1,
                TestUtils.image("skill1", 200), true
            );
            SkillResponseDTO skill2 = new SkillResponseDTO(
                "2", "Skill 2", ZonedDateTime.now(), ZonedDateTime.now(), "Desc 2", 4.0, 2,
                TestUtils.image("skill2", 200), true
            );

            JsonNode skill1Node = objectMapper.convertValue(skill1, JsonNode.class);

            WebhookEventDTO webhookEvent1 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "skills", skill1Node
            );
            WebhookEventDTO webhookEvent2 = new WebhookEventDTO(
                "ENTRY_UPDATE", ZonedDateTime.now(), "skills", skill1Node
            );

            SkillsGroupResponseDTO skillsGroup = new SkillsGroupResponseDTO(
                "100", "Skills Group", ZonedDateTime.now(), ZonedDateTime.now(), "Group desc",
                4.5, 1, TestUtils.image("skillGroup", 300), List.of(skill1, skill2)
            );

            IndexSiteRequest siteIndexRequest1 = new IndexSiteRequest(
                "skill_1", "/skills-groups/100#1", "Skill 1", "Skills",
                "uploads/skill1-thumb.jpg", "Desc 1", "Group desc"
            );
            IndexSiteRequest siteIndexRequest2 = new IndexSiteRequest(
                "skill_2", "/skills-groups/100#2", "Skill 2", "Skills",
                "uploads/skill2-thumb.jpg", "Desc 2", "Group desc"
            );

            when(cmsRestApi.getAllSkillsGroups())
                .thenReturn(CompletableFuture.completedFuture(List.of(skillsGroup)));
            skillsGroupMapperMock.when(() -> SkillsGroupMapper.toSiteIndexRequests(skillsGroup))
                .thenReturn(List.of(siteIndexRequest1, siteIndexRequest2));

            kafkaEventConsumer.consumeEvents(List.of(webhookEvent1, webhookEvent2));

            verify(indexSiteUseCase).indexSites(List.of(siteIndexRequest1, siteIndexRequest2));
        }
    }
}