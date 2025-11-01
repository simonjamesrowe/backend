package com.simonjamesrowe.backend.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.ProfileResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.model.cms.dto.SocialMediaResponseDTO;

import java.util.List;

public interface ICmsRestApi {

    List<BlogResponseDTO> getAllBlogs();
    List<JobResponseDTO> getAllJobs();
    List<SkillResponseDTO> getAllSkills();
    List<SkillsGroupResponseDTO> getAllSkillsGroups();
    List<SkillsGroupResponseDTO> getSkillsGroupBySkillId(String skillId);
    List<ProfileResponseDTO> getProfiles();
    List<SocialMediaResponseDTO> getAllSocialMedias();
}
