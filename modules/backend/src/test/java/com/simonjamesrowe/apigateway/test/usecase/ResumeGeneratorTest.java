package com.simonjamesrowe.apigateway.test.usecase;

import com.simonjamesrowe.apigateway.core.model.ResumeData;
import com.simonjamesrowe.apigateway.core.usecase.ResumeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class ResumeGeneratorTest {

    @Test
    void shouldGenerateExpectedPdfByteArray() throws Exception {
        ResumeData testData = new ResumeData(
            "Test McTest",
            "+4478923234234",
            "test@gmail.com",
            "Cloud Native Developer & Architect",
            List.of(
                new ResumeData.Skill("Java", 9.0),
                new ResumeData.Skill("Spring", 8.5)
            ),
            List.of(
                new ResumeData.Job(
                    "Principal Developer",
                    "Company 1",
                    "http://job1",
                    LocalDate.parse("2021-01-01"),
                    null,
                    "Doing principal developer things",
                    "London"
                ),
                new ResumeData.Job(
                    "Senior Developer Developer",
                    "Company 2",
                    "http://job1",
                    LocalDate.parse("2020-01-01"),
                    LocalDate.parse("2020-12-31"),
                    "Doing Senior Developer Things",
                    "London"
                )
            ),
            List.of(
                new ResumeData.Job(
                    "Computer Science",
                    "University of Newcastle",
                    "http://link",
                    LocalDate.parse("2002-01-01"),
                    LocalDate.parse("2004-12-31"),
                    "Bachelor of Computer Science WAM 82",
                    "Newcastle"
                )
            ),
            List.of(
                new ResumeData.Link("link1"),
                new ResumeData.Link("link2")
            )
        );

        byte[] result = ResumeGenerator.generate(testData);
        byte[] expected = Files.readAllBytes(new ClassPathResource("resume.pdf").getFile().toPath());
        assertThat(result.length).isCloseTo(expected.length, withPercentage(10.0));
    }
}