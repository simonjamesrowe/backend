package com.simonjamesrowe.searchservice.test.entrypoints.scheduled;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase;
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase;
import com.simonjamesrowe.searchservice.dataproviders.cms.ICmsRestApi;
import com.simonjamesrowe.searchservice.entrypoints.scheduled.CmsSynchronization;
import com.simonjamesrowe.searchservice.mapper.BlogMapper;
import com.simonjamesrowe.searchservice.mapper.JobMapper;
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsSynchronizationTest {

    @Mock
    private ICmsRestApi cmsRestApi;

    @Mock
    private IndexSiteUseCase indexSiteUseCase;

    @Mock
    private IndexBlogUseCase indexBlogUseCase;

    @Mock
    private Environment environment;

    @InjectMocks
    private CmsSynchronization cmsSynchronization;

    @Test
    void shouldIndexBlogDocuments() {
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

            when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);
            when(cmsRestApi.getAllBlogs())
                    .thenReturn(CompletableFuture.completedFuture(List.of(blog1, blog2)));

            IndexBlogRequest indexBlogRequest1 = new IndexBlogRequest(
                "1", "Blog 1", "Short desc 1", "Content 1", List.of(), List.of(),
                "thumb1.jpg", "small1.jpg", "med1.jpg", LocalDate.now(), true
            );
            IndexBlogRequest indexBlogRequest2 = new IndexBlogRequest(
                "2", "Blog 2", "Short desc 2", "Content 2", List.of(), List.of(),
                "thumb2.jpg", "small2.jpg", "med2.jpg", LocalDate.now(), true
            );

            blogMapperMock.when(() -> BlogMapper.toBlogIndexRequest(any(BlogResponseDTO.class)))
                .thenReturn(indexBlogRequest1, indexBlogRequest2);

            cmsSynchronization.syncBlogDocuments();

            verify(indexBlogUseCase)
                    .indexBlogs(List.of(indexBlogRequest1, indexBlogRequest2));
        }
    }

    @Test
    void shouldIndexSiteDocuments() {
        try (MockedStatic<BlogMapper> blogMapperMock = Mockito.mockStatic(BlogMapper.class);
             MockedStatic<JobMapper> jobMapperMock = Mockito.mockStatic(JobMapper.class);
             MockedStatic<SkillsGroupMapper> skillsGroupMapperMock = Mockito.mockStatic(SkillsGroupMapper.class)) {

            when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);

            // Setup blogs
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
            when(cmsRestApi.getAllBlogs())
                    .thenReturn(CompletableFuture.completedFuture(List.of(blog1, blog2)));

            IndexSiteRequest siteIndexRequest1 = new IndexSiteRequest("blog_1", "/blogs/1",
                    "Blog 1", "Blogs", "thumb1.jpg", "Short desc 1", "Content 1");
            IndexSiteRequest siteIndexRequest2 = new IndexSiteRequest("blog_2", "/blogs/2",
                    "Blog 2", "Blogs", "thumb2.jpg", "Short desc 2", "Content 2");
            blogMapperMock.when(() -> BlogMapper.toSiteIndexRequest(any(BlogResponseDTO.class)))
                .thenReturn(siteIndexRequest1, siteIndexRequest2);

            // Setup jobs
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
            when(cmsRestApi.getAllJobs())
                    .thenReturn(CompletableFuture.completedFuture(List.of(job1, job2)));

            IndexSiteRequest siteIndexRequest3 = new IndexSiteRequest("job_1", "/jobs/1",
                    "Job 1", "Jobs", "company1.jpg", "Short desc 1", "Long desc 1");
            IndexSiteRequest siteIndexRequest4 = new IndexSiteRequest("job_2", "/jobs/2",
                    "Job 2", "Jobs", "company2.jpg", "Short desc 2", "Long desc 2");
            jobMapperMock.when(() -> JobMapper.toIndexSiteRequest(any(JobResponseDTO.class)))
                .thenReturn(siteIndexRequest3, siteIndexRequest4);

            // Setup skills groups
            SkillResponseDTO skill1 = new SkillResponseDTO("1", "Skill 1",
                    ZonedDateTime.now(), ZonedDateTime.now(), "Desc 1", 5.0, 1,
                    TestUtils.image("skill1", 200), true);
            SkillResponseDTO skill2 = new SkillResponseDTO("2", "Skill 2",
                    ZonedDateTime.now(), ZonedDateTime.now(), "Desc 2", 4.0, 2,
                    TestUtils.image("skill2", 200), true);
            SkillResponseDTO skill3 = new SkillResponseDTO("3", "Skill 3",
                    ZonedDateTime.now(), ZonedDateTime.now(), "Desc 3", 3.0, 3,
                    TestUtils.image("skill3", 200), true);

            SkillsGroupResponseDTO skillsGroup1 = new SkillsGroupResponseDTO("100",
                    "Skills Group 1", ZonedDateTime.now(), ZonedDateTime.now(),
                    "Group desc 1", 4.5, 1, TestUtils.image("skillGroup1", 300),
                    List.of(skill1, skill2));
            SkillsGroupResponseDTO skillsGroup2 = new SkillsGroupResponseDTO("200",
                    "Skills Group 2", ZonedDateTime.now(), ZonedDateTime.now(),
                    "Group desc 2", 3.5, 2, TestUtils.image("skillGroup2", 300),
                    List.of(skill3));

            when(cmsRestApi.getAllSkillsGroups())
                    .thenReturn(CompletableFuture.completedFuture(
                            List.of(skillsGroup1, skillsGroup2)));

            IndexSiteRequest siteIndexRequest5 = new IndexSiteRequest("skill_1",
                    "/skills-groups/100#1", "Skill 1", "Skills", "skill1.jpg",
                    "Desc 1", "Group desc 1");
            IndexSiteRequest siteIndexRequest6 = new IndexSiteRequest("skill_2",
                    "/skills-groups/100#2", "Skill 2", "Skills", "skill2.jpg",
                    "Desc 2", "Group desc 1");
            IndexSiteRequest siteIndexRequest7 = new IndexSiteRequest("skill_3",
                    "/skills-groups/200#3", "Skill 3", "Skills", "skill3.jpg",
                    "Desc 3", "Group desc 2");

            skillsGroupMapperMock.when(() -> SkillsGroupMapper.toSiteIndexRequests(skillsGroup1))
                .thenReturn(List.of(siteIndexRequest5, siteIndexRequest6));
            skillsGroupMapperMock.when(() -> SkillsGroupMapper.toSiteIndexRequests(skillsGroup2))
                .thenReturn(List.of(siteIndexRequest7));

            cmsSynchronization.syncSiteDocuments();

            verify(indexSiteUseCase).indexSites(List.of(
                siteIndexRequest1, siteIndexRequest2,
                siteIndexRequest3, siteIndexRequest4,
                siteIndexRequest5, siteIndexRequest6, siteIndexRequest7
            ));
        }
    }
}