package com.simonjamesrowe.apigateway.test.dataproviders.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.simonjamesrowe.apigateway.core.model.Email;
import com.simonjamesrowe.apigateway.dataproviders.sendgrid.SendGridEmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendGridEmailSenderTest {

    @Mock
    private SendGrid sendGrid;

    private SendGridEmailSender sendGridEmailSender;

    @BeforeEach
    void before() {
        sendGridEmailSender = new SendGridEmailSender(sendGrid, "from@from.com", "to@to.com");
    }

    @Test
    void shouldSendEmail() throws Exception {
        Email email = new Email(
            "subject",
            "text/plain",
            "Email body"
        );

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);

        sendGridEmailSender.sendEmail(email);

        verify(sendGrid).api(requestCaptor.capture());
        Request capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getEndpoint()).isEqualTo("mail/send");
        assertThat(capturedRequest.getMethod()).isEqualTo(Method.POST);
        String expectedBody =
            "{\"from\":{\"email\":\"from@from.com\"},\"subject\":\"subject\","
            + "\"personalizations\":[{\"to\":[{\"email\":\"to@to.com\"}]}],"
            + "\"content\":[{\"type\":\"text/plain\",\"value\":\"Email body\"}]}";
        JSONAssert.assertEquals(capturedRequest.getBody(), expectedBody, true);
    }

    @Test
    void shouldThrowExceptionIfEmailFailsToSend() throws Exception {
        Email email = new Email(
            "subject",
            "text/plain",
            "Email body"
        );

        when(sendGrid.api(any())).thenThrow(new IOException("exception"));

        assertThrows(RuntimeException.class, () -> sendGridEmailSender.sendEmail(email));
    }
}