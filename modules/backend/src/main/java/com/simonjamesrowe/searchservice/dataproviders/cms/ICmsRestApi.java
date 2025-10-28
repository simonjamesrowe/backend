package com.simonjamesrowe.searchservice.dataproviders.cms;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICmsRestApi {
    CompletableFuture<List<BlogResponseDTO>> getAllBlogs();

    CompletableFuture<List<JobResponseDTO>> getAllJobs();

    CompletableFuture<List<SkillsGroupResponseDTO>> getAllSkillsGroups();

    CompletableFuture<List<SkillsGroupResponseDTO>> getSkillsGroupBySkillId(String skillId);
}