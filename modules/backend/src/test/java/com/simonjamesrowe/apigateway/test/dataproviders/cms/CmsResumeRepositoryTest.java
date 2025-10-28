package com.simonjamesrowe.apigateway.test.dataproviders.cms;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.simonjamesrowe.apigateway.config.RestClientConfig;
import com.simonjamesrowe.apigateway.dataproviders.cms.CmsRestApi;
import com.simonjamesrowe.apigateway.dataproviders.cms.CmsResumeRepository;
import com.simonjamesrowe.apigateway.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest(properties = {"spring.main.web-application-type=none"})
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("cms")
@Import({CmsRestApi.class, CmsResumeRepository.class, RestClientConfig.class})
class CmsResumeRepositoryTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private CmsResumeRepository cmsResumeRepository;

    @BeforeEach
    void setup() {
        TestUtils.mockGet(wireMockServer, "/profiles", "profile.json");
        TestUtils.mockGet(wireMockServer, "/skills", "skills.json");
        TestUtils.mockGet(wireMockServer, "/jobs", "jobs.json");
        TestUtils.mockGet(wireMockServer, "/social-medias", "social-media.json");
    }

    @Test
    void shouldReturnCorrectResumeData() throws Exception {
        var result = cmsResumeRepository.getResumeData();

        assertThat(result.getName()).isEqualTo("Simon Rowe");
        assertThat(result.getPhone()).isEqualTo("+447909083522");
        assertThat(result.getEmail()).isEqualTo("simon.rowe@gmail.com");
        assertThat(result.getHeadline())
            .isEqualTo("PASSIONATE ABOUT BUILDING CLOUD NATIVE APPS UTILIZING SPRING, KAFKA AND KUBERNETES.");
        assertThat(result.getSkills()).hasSize(11);
        assertThat(result.getSkills().get(0)).hasFieldOrPropertyWithValue("name", "Java 9-11");
        assertThat(result.getSkills().get(0)).hasFieldOrPropertyWithValue("rating", 9.0);
        assertThat(result.getJobs()).hasSize(6);
        assertThat(result.getJobs().get(0)).hasFieldOrPropertyWithValue("role", "Senior Developer");
        assertThat(result.getJobs().get(0)).hasFieldOrPropertyWithValue("company", "Y-Tree");
        assertThat(result.getJobs().get(0)).hasFieldOrPropertyWithValue(
            "link",
            "https://www.simonjamesrowe.com/jobs/5eedd4803c8d74001e4497f5"
        );
        assertThat(result.getJobs().get(0)).hasFieldOrPropertyWithValue("start", LocalDate.parse("2020-05-04"));
        assertThat(result.getJobs().get(0)).hasFieldOrPropertyWithValue("location", "London");
        assertThat(result.getEducation()).hasSize(1);
        assertThat(result.getEducation().get(0)).hasFieldOrPropertyWithValue("role", "Computer Science");
        assertThat(result.getEducation().get(0)).hasFieldOrPropertyWithValue("company", "University of Newcastle");
        assertThat(result.getEducation().get(0))
            .hasFieldOrPropertyWithValue("shortDescription", "Bachelor of Computer Science");
        assertThat(result.getEducation().get(0)).hasFieldOrPropertyWithValue("start", LocalDate.parse("2002-01-01"));
        assertThat(result.getEducation().get(0)).hasFieldOrPropertyWithValue("location", "Newcastle");
        assertThat(result.getLinks()).hasSize(4);
    }
}