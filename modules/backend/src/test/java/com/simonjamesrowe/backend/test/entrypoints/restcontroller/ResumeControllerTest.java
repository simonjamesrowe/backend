package com.simonjamesrowe.backend.test.entrypoints.restcontroller;

import com.simonjamesrowe.backend.config.SecurityConfig;
import com.simonjamesrowe.backend.core.usecase.IResumeUseCase;
import com.simonjamesrowe.backend.entrypoints.restcontroller.ResumeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
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
