package com.simonjamesrowe.searchservice.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import org.springframework.beans.factory.annotation.Value;
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

    public CmsRestApi(RestClient restClient, @Value("${cms.url}") String cmsUrl) {
        this.restClient = restClient;
        this.cmsUrl = cmsUrl;
    }

    @Override
    public CompletableFuture<List<BlogResponseDTO>> getAllBlogs() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/blogs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<BlogResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<JobResponseDTO>> getAllJobs() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/jobs")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<SkillsGroupResponseDTO>> getAllSkillsGroups() {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/skills-groups")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() { })
        );
    }

    @Override
    public CompletableFuture<List<SkillsGroupResponseDTO>> getSkillsGroupBySkillId(String skillId) {
        return CompletableFuture.supplyAsync(() ->
            restClient.get()
                .uri(cmsUrl + "/skills-groups?skill._id={id}", skillId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkillsGroupResponseDTO>>() { })
        );
    }
}