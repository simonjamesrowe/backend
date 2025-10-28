package com.simonjamesrowe.apigateway.test.usecase;

import com.simonjamesrowe.apigateway.core.model.ResumeData;
import com.simonjamesrowe.apigateway.core.repository.ResumeRepository;
import com.simonjamesrowe.apigateway.core.usecase.ResumeGenerator;
import com.simonjamesrowe.apigateway.core.usecase.ResumeUseCase;
import com.simonjamesrowe.apigateway.test.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearAllCaches;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeUseCaseTest {

    @Mock
    private ResumeRepository resumeRepository;

    private MockedStatic<ResumeGenerator> resumeGeneratorMock;

    @BeforeEach
    void before() {
        resumeGeneratorMock = mockStatic(ResumeGenerator.class);
    }

    @AfterEach
    void after() {
        resumeGeneratorMock.close();
        clearAllCaches();
    }

    @Test
    void shouldInitializeResumeDataInConstructor() throws Exception {
        ResumeData testData = TestUtils.randomObject(ResumeData.class);
        when(resumeRepository.getResumeData()).thenReturn(testData);
        byte[] bytes = "initial resume bytes".getBytes();
        resumeGeneratorMock.when(() -> ResumeGenerator.generate(testData)).thenReturn(bytes);

        assertThat(new ResumeUseCase(resumeRepository).getResume()).isEqualTo(bytes);
    }

    @Test
    void shouldRegenerateResume() throws Exception {
        ResumeData testData = TestUtils.randomObject(ResumeData.class);
        when(resumeRepository.getResumeData()).thenReturn(testData);
        byte[] bytes = "initial resume bytes".getBytes();
        resumeGeneratorMock.when(() -> ResumeGenerator.generate(testData)).thenReturn(bytes);

        ResumeUseCase useCase = new ResumeUseCase(resumeRepository);
        ResumeData newTestData = TestUtils.randomObject(ResumeData.class);
        when(resumeRepository.getResumeData()).thenReturn(newTestData);
        byte[] newBytes = "new resume bytes".getBytes();
        resumeGeneratorMock.when(() -> ResumeGenerator.generate(newTestData)).thenReturn(newBytes);

        useCase.regenerateResume();
        assertThat(useCase.getResume()).isEqualTo(newBytes);
    }

    private void clearAllCaches() {
        // Helper method to clear any static caches if needed
    }
}