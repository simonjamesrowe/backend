package com.simonjamesrowe.backend.test.dataproviders.cms;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillsGroupResponseDTO;
import com.simonjamesrowe.backend.config.RestClientConfiguration;
import com.simonjamesrowe.backend.dataproviders.cms.CmsRestApi;
import com.simonjamesrowe.backend.dataproviders.cms.ICmsRestApi;
import com.simonjamesrowe.backend.test.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@JsonTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("cms")
@Import({CmsRestApi.class, RestClientConfiguration.class})
class CmsRestApiTest {

    @Autowired
    private ICmsRestApi cmsRestApi;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setupWiremock() {
        TestUtils.mockGet(wireMockServer, "/blogs", "getAllBlogs.json");
        TestUtils.mockGet(wireMockServer, "/skills-groups", "getAllSkillsGroups.json");
        TestUtils.mockGet(wireMockServer, "/jobs", "getAllJobs.json");
    }

    @Test
    void shouldReturnAllBlogsFromCms() {
        List<BlogResponseDTO> result = cmsRestApi.getAllBlogs();

        Assertions.assertThat(result).hasSize(10);
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("id", "5f0215c69d8081001fd38fa1");
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("published", true);
        Assertions.assertThat(result.get(0).tags().stream()
                .map(tag -> tag.name()).collect(Collectors.toList()))
            .contains("Kubernetes", "Jenkins", "Strapi", "TLS", "MongoDB", "React");
        Assertions.assertThat(result.get(0).skills().stream()
                .map(skill -> skill.name()).collect(Collectors.toList()))
            .contains("Jenkins Pipeline");
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("title",
                        "Creating a rich web app that can be hosted from home");
        Assertions.assertThat(result.get(0).content()).isNotNull();
        Assertions.assertThat(result.get(0).shortDescription()).isNotNull();
    }

    @Test
    void shouldReturnAllJobsFromCms() {
        List<JobResponseDTO> result = cmsRestApi.getAllJobs();

        Assertions.assertThat(result).hasSize(9);
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("id", "5e53704f11c196001d06f914");
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("title", "Software Engineering Lead");
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("company", "Upp Technologies");
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("companyUrl", "https://upp.ai");
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("startDate", LocalDate.parse("2019-04-15"));
        Assertions.assertThat(result.get(0))
                .hasFieldOrPropertyWithValue("endDate", LocalDate.parse("2020-05-01"));
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("includeOnResume", true);
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("education", false);
    }

    @Test
    void shouldReturnAllSkillsGroupsFromCms() {
        List<SkillsGroupResponseDTO> result = cmsRestApi.getAllSkillsGroups();

        Assertions.assertThat(result).hasSize(9);
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("name", "Java / Kotlin");
        Assertions.assertThat(result.get(0)).hasFieldOrPropertyWithValue("rating", 9.2);
        Assertions.assertThat(result.get(0).skills()).hasSize(3);
    }
}
