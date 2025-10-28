package com.simonjamesrowe.apigateway.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.ProfileResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SocialMediaResponseDTO;

import java.util.List;

public interface ICmsRestApi {

    List<JobResponseDTO> getAllJobs();

    List<SkillResponseDTO> getAllSkills();

    List<ProfileResponseDTO> getProfiles();

    List<SocialMediaResponseDTO> getAllSocialMedias();
}