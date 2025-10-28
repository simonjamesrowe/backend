package com.simonjamesrowe.searchservice.test.mapper;

import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.mapper.SkillsGroupMapper;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

class SkillsGroupMapperTest {

    @Test
    void shouldConvertToIndexSiteRequests() {
        SkillResponseDTO skill1 = new SkillResponseDTO(
            "1",
            "Skill 1",
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            "Description 1",
            5.0,
            1,
            TestUtils.image("skill1", 200),
            true
        );
        SkillResponseDTO skill2 = new SkillResponseDTO(
            "2",
            "Skill 2",
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            "Description 2",
            4.5,
            2,
            TestUtils.image("skill2", 200),
            false
        );
        SkillsGroupResponseDTO skillsGroup = new SkillsGroupResponseDTO(
            "100",
            "Skills Group",
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            "Group Description",
            4.8,
            1,
            TestUtils.image("skillGroup", 300),
            List.of(skill1, skill2)
        );

        List<IndexSiteRequest> expected = List.of(
            new IndexSiteRequest(
                "skill_1",
                "/skills-groups/100#1",
                "Skill 1",
                "Skills",
                "uploads/skill1-thumb.jpg",
                "Description 1",
                "Group Description"
            ),
            new IndexSiteRequest(
                "skill_2",
                "/skills-groups/100#2",
                "Skill 2",
                "Skills",
                "uploads/skill2-thumb.jpg",
                "Description 2",
                "Group Description"
            )
        );

        Assertions.assertThat(SkillsGroupMapper.toSiteIndexRequests(skillsGroup)).isEqualTo(expected);
    }
}