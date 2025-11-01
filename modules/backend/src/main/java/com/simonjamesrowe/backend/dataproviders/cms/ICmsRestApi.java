package com.simonjamesrowe.backend.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.ProfileResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.model.cms.dto.SocialMediaResponseDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICmsRestApi {

    // Sync methods (used by API Gateway)
    List<JobResponseDTO> getAllJobs();
    List<SkillResponseDTO> getAllSkills();
    List<ProfileResponseDTO> getProfiles();
    List<SocialMediaResponseDTO> getAllSocialMedias();

    // Async methods (used by Search Service)
    CompletableFuture<List<BlogResponseDTO>> getAllBlogsAsync();
    CompletableFuture<List<JobResponseDTO>> getAllJobsAsync();
    CompletableFuture<List<SkillsGroupResponseDTO>> getAllSkillsGroupsAsync();
    CompletableFuture<List<SkillsGroupResponseDTO>> getSkillsGroupBySkillIdAsync(String skillId);
}
