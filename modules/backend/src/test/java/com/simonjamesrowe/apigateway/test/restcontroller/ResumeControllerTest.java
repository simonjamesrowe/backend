package com.simonjamesrowe.apigateway.test.restcontroller;

import com.simonjamesrowe.apigateway.config.SecurityConfig;
import com.simonjamesrowe.apigateway.core.usecase.IResumeUseCase;
import com.simonjamesrowe.apigateway.entrypoints.restcontroller.ResumeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResumeController.class)
@Import(SecurityConfig.class)
@DirtiesContext
class ResumeControllerTest {

    @MockBean
    private IResumeUseCase resumeUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnFile() throws Exception {
        byte[] bytes = Files.readAllBytes(new ClassPathResource("resume.pdf").getFile().toPath());
        when(resumeUseCase.getResume()).thenReturn(bytes);

        byte[] actual = mockMvc.perform(get("/resume"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        assertThat(actual).isEqualTo(bytes);
    }
}