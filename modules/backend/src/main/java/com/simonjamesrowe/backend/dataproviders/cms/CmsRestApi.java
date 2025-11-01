package com.simonjamesrowe.backend.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.ProfileResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.model.cms.dto.SocialMediaResponseDTO;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CmsRestApi implements ICmsRestApi {

    private final RestClient restClient;
    private final String cmsUrl;

    public CmsRestApi(RestClient restClient, CmsProperties cmsProperties) {
        this.restClient = restClient;
        this.cmsUrl = cmsProperties.url();
    }

    // Synchronous methods (from API Gateway)
    @NewSpan("http-getAllJobs-cms")
    @Override
    public List<JobResponseDTO> getAllJobs() {
        return restClient.get()
                .uri(cmsUrl + "/jobs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobResponseDTO>>() { });
    }

    @NewSpan("http-getAllSkillsGroups-cms")
    @Override
    public List<SkillResponseDTO> getAllSkills() {
        return restClient.get()
                .uri(cmsUrl + "/skills")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkillResponseDTO>>() { });
    }

    @NewSpan("http-getProfiles-cms")
    @Override
    public List<ProfileResponseDTO> getProfiles() {
        return restClient.get()
                .uri(cmsUrl + "/profiles")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProfileResponseDTO>>() { });
    }

    @NewSpan("http-getSocialMedias-cms")
    @Override
    public List<SocialMediaResponseDTO> getAllSocialMedias() {
        return restClient.get()
                .uri(cmsUrl + "/social-medias")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SocialMediaResponseDTO>>() { });
    }

    // Asynchronous methods (from Search Service)
    @Override
    public CompletableFuture<List<BlogResponseDTO>> getAllBlogsAsync() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/blogs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<BlogResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<JobResponseDTO>> getAllJobsAsync() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/jobs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<SkillsGroupResponseDTO>> getAllSkillsGroupsAsync() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/skills-groups")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<SkillsGroupResponseDTO>> getSkillsGroupBySkillIdAsync(String skillId) {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/skills-groups?skill._id={id}", skillId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() { })
        );
    }
}
