package com.simonjamesrowe.backend.test.entrypoints.restcontroller;

import com.simonjamesrowe.backend.config.SecurityConfig;
import com.simonjamesrowe.backend.core.model.ContactUsRequest;
import com.simonjamesrowe.backend.core.usecase.ContactUseCase;
import com.simonjamesrowe.backend.entrypoints.restcontroller.ContactUsController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContactUsController.class)
@Import(SecurityConfig.class)
@DirtiesContext
class ContactUsControllerTest {

    @MockitoBean
    private ContactUseCase contactUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSuccessfullyHandleContactUsRequest() throws Exception {
        mockMvc.perform(post("/contact-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Referer", "Referer")
                        .content("""
                                 {
                                  "firstName" : "Simon",
                                  "lastName": "Rowe",
                                  "subject" : "Hi",
                                  "emailAddress" : "simon.rowe@gmail.com",
                                  "content" : "This is a test message"
                                }
                                """.trim()))
                .andExpect(status().isOk());

        verify(contactUseCase).contactUs(
                new ContactUsRequest(
                        "Simon",
                        "Rowe",
                        "simon.rowe@gmail.com",
                        "Hi",
                        "This is a test message",
                        "Referer"
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "'',Rowe,Hello,simon.rowe@gmail.com,Message",
            "Simon,'',Hello,simon.rowe@gmail.com,Message",
            "Simon,Rowe,'',simon.rowe@gmail.com,Message",
            "Simon,Rowe,Hello,'',Message",
            "Simon,Rowe,Hello,simon.rowe@gmail.com,''",
            "Simon,Rowe,Hello,invalidEmailAddress,Message"
    })
    void clientErrorShouldOccurForInvalidFields(
            String firstName,
            String lastName,
            String subject,
            String email,
            String content
    ) throws Exception {
        String requestBody = String.format("""
                {
                  "firstName" : "%s",
                  "lastName": "%s",
                  "subject" : "%s",
                  "emailAddress" : "%s",
                  "content" : "%s"
                }
                """, firstName, lastName, subject, email, content).trim();

        mockMvc.perform(post("/contact-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Referer", "Referer")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
