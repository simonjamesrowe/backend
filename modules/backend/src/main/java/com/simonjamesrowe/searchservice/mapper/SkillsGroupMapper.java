package com.simonjamesrowe.searchservice.mapper;

import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;

import java.util.List;
import java.util.stream.Collectors;

public final class SkillsGroupMapper {

    private SkillsGroupMapper() {
        // Utility class
    }

    public static List<IndexSiteRequest> toSiteIndexRequests(SkillsGroupResponseDTO skillsGroup) {
        return skillsGroup.skills().stream()
            .map(skill -> {
                String imageUrl = "";
                if (skill.image() != null && skill.image().formats() != null
                    && skill.image().formats().thumbnail() != null) {
                    imageUrl = skill.image().formats().thumbnail().url();
                }

                return new IndexSiteRequest(
                    "skill_" + skill.id(),
                    "/skills-groups/" + skillsGroup.id() + "#" + skill.id(),
                    skill.name(),
                    "Skills",
                    imageUrl,
                    skill.description() != null ? skill.description() : "",
                    skillsGroup.description()
                );
            })
            .collect(Collectors.toList());
    }
}