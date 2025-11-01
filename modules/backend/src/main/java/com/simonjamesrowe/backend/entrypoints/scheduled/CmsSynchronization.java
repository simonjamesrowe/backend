package com.simonjamesrowe.backend.entrypoints.scheduled;

import com.simonjamesrowe.backend.core.model.IndexSiteRequest;
import com.simonjamesrowe.backend.core.usecase.IndexBlogUseCase;
import com.simonjamesrowe.backend.core.usecase.IndexSiteUseCase;
import com.simonjamesrowe.backend.dataproviders.cms.ICmsRestApi;
import com.simonjamesrowe.backend.mapper.BlogMapper;
import com.simonjamesrowe.backend.mapper.JobMapper;
import com.simonjamesrowe.backend.mapper.SkillsGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CmsSynchronization implements ICmsSynchronization {

    private static final Logger LOG = LoggerFactory.getLogger(CmsSynchronization.class);
    private static final long ONE_MINUTE = 60 * 1000L;
    private static final long FOUR_HOURS = 4 * 60 * 60 * 1000L;

    private final ICmsRestApi cmsRestApi;
    private final IndexBlogUseCase indexBlogUseCase;
    private final IndexSiteUseCase indexSiteUseCase;
    private final Environment env;

    public CmsSynchronization(ICmsRestApi cmsRestApi,
                             IndexBlogUseCase indexBlogUseCase,
                             IndexSiteUseCase indexSiteUseCase,
                             Environment env) {
        this.cmsRestApi = cmsRestApi;
        this.indexBlogUseCase = indexBlogUseCase;
        this.indexSiteUseCase = indexSiteUseCase;
        this.env = env;
    }

    @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
    @Override
    public void syncBlogDocuments() {
        if (!env.acceptsProfiles(Profiles.of("cloud"))) {
            return;
        }

        try {
            LOG.info("Synchronising blog documents from cms");
            cmsRestApi.getAllBlogsAsync().thenAccept(allBlogs -> {
                var blogRequests = allBlogs.stream()
                    .map(BlogMapper::toBlogIndexRequest)
                    .collect(Collectors.toList());
                indexBlogUseCase.indexBlogs(blogRequests);
            }).join();
        } catch (Exception e) {
            LOG.error("Error synchronizing blog documents", e);
        }
    }

    @Scheduled(initialDelay = ONE_MINUTE, fixedDelay = FOUR_HOURS)
    @Override
    public void syncSiteDocuments() {
        if (!env.acceptsProfiles(Profiles.of("cloud"))) {
            return;
        }

        try {
            LOG.info("Synchronising site documents from cms");

            CompletableFuture.allOf(
                cmsRestApi.getAllBlogsAsync(),
                cmsRestApi.getAllJobsAsync(),
                cmsRestApi.getAllSkillsGroupsAsync()
            ).thenApply(ignored -> {
                var allSiteDocuments = new ArrayList<IndexSiteRequest>();

                // Add blog site documents
                cmsRestApi.getAllBlogsAsync().thenAccept(allBlogs -> {
                    allBlogs.forEach(blog ->
                        allSiteDocuments.add(BlogMapper.toSiteIndexRequest(blog))
                    );
                }).join();

                // Add job site documents
                cmsRestApi.getAllJobsAsync().thenAccept(allJobs -> {
                    allJobs.forEach(job ->
                        allSiteDocuments.add(JobMapper.toIndexSiteRequest(job))
                    );
                }).join();

                // Add skills site documents
                cmsRestApi.getAllSkillsGroupsAsync().thenAccept(allSkillsGroups -> {
                    allSkillsGroups.forEach(skillsGroup -> {
                        List<IndexSiteRequest> skillRequests =
                                SkillsGroupMapper.toSiteIndexRequests(skillsGroup);
                        allSiteDocuments.addAll(skillRequests);
                    });
                }).join();

                LOG.info("Indexing {} site documents", allSiteDocuments.size());
                indexSiteUseCase.indexSites(allSiteDocuments);

                return allSiteDocuments;
            }).join();
        } catch (Exception e) {
            LOG.error("Error synchronizing site documents", e);
        }
    }
}