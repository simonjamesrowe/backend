package com.simonjamesrowe.searchservice.test.mapper;

import com.simonjamesrowe.model.cms.dto.JobResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.mapper.JobMapper;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;

class JobMapperTest {

    @Test
    void shouldConvertToIndexSiteRequest() {
        JobResponseDTO input = new JobResponseDTO(
            "job1",
            "Software Engineer",
            "TechCorp",
            "https://techcorp.com",
            "Software development role",
            "Full stack development position",
            TestUtils.image("companyImg", 200),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2022, 12, 31),
            true,
            false,
            "London"
        );

        IndexSiteRequest expected = new IndexSiteRequest(
            "job_job1",
            "/jobs/job1",
            "Software Engineer (TechCorp) - 2020 - 2022",
            "Jobs",
            "uploads/companyImg-thumb.jpg",
            "Software development role",
            "Full stack development position"
        );

        Assertions.assertThat(JobMapper.toIndexSiteRequest(input)).isEqualTo(expected);
    }

    @Test
    void shouldConvertToIndexSiteRequestWithNullEndDate() {
        JobResponseDTO input = new JobResponseDTO(
            "job2",
            "Senior Developer",
            "StartupCorp",
            "https://startup.com",
            "Current role",
            "Leading development team",
            TestUtils.image("companyImg", 200),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            LocalDate.of(2023, 1, 1),
            null,
            true,
            false,
            "Remote"
        );

        IndexSiteRequest expected = new IndexSiteRequest(
            "job_job2",
            "/jobs/job2",
            "Senior Developer (StartupCorp) - 2023 - Present",
            "Jobs",
            "uploads/companyImg-thumb.jpg",
            "Current role",
            "Leading development team"
        );

        Assertions.assertThat(JobMapper.toIndexSiteRequest(input)).isEqualTo(expected);
    }
}