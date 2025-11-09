package com.simonjamesrowe.backend.test.core.usecase;

import com.simonjamesrowe.backend.core.model.ContactUsRequest;
import com.simonjamesrowe.backend.core.model.Email;
import com.simonjamesrowe.backend.core.repository.EmailSender;
import com.simonjamesrowe.backend.core.usecase.ContactUseCase;
import com.simonjamesrowe.backend.test.TestUtils;
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