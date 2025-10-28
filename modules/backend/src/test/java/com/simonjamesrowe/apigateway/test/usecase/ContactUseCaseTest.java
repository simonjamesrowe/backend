package com.simonjamesrowe.apigateway.test.usecase;

import com.simonjamesrowe.apigateway.core.model.ContactUsRequest;
import com.simonjamesrowe.apigateway.core.model.Email;
import com.simonjamesrowe.apigateway.core.repository.EmailSender;
import com.simonjamesrowe.apigateway.core.usecase.ContactUseCase;
import com.simonjamesrowe.apigateway.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactUseCaseTest {

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private ContactUseCase contactUseCase;

    @Test
    void shouldGenerateEmailBodyAndSendEmail() throws Exception {
        ContactUsRequest request = TestUtils.randomObject(ContactUsRequest.class);
        contactUseCase.contactUs(request);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailSender).sendEmail(emailCaptor.capture());

        Email capturedEmail = emailCaptor.getValue();
        assertThat(capturedEmail.getContentType()).isEqualTo("text/plain");
        assertThat(capturedEmail.getSubject()).isEqualTo(request.getSubject());
        assertThat(capturedEmail.getBody()).isEqualTo(
            String.format("""
            A message has been sent from the site: %s
            Email Address: %s
            Name: %s %s
            Content: %s""",
            request.getReferrer(),
            request.getEmail(),
            request.getFirst(),
            request.getLast(),
            request.getMessage())
        );
    }
}