package com.simonjamesrowe.apigateway.dataproviders.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.simonjamesrowe.apigateway.core.model.Email;
import com.simonjamesrowe.apigateway.core.repository.EmailSender;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SendGridEmailSender implements EmailSender {

    private final SendGrid sendGrid;
    private final String fromAddress;
    private final String toAddress;

    public SendGridEmailSender(
            SendGrid sendGrid,
            @Value("${sendgrid.email.from:}") String fromAddress,
            @Value("${sendgrid.email.to:}") String toAddress) {
        this.sendGrid = sendGrid;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
    }

    @NewSpan("sendEmail")
    @Override
    public void sendEmail(Email email) {
        try {
            Mail mail = new Mail(
                    new com.sendgrid.helpers.mail.objects.Email(fromAddress),
                    email.getSubject(),
                    new com.sendgrid.helpers.mail.objects.Email(toAddress),
                    new Content(email.getContentType(), email.getBody())
            );
            sendMail(mail);
        } catch (IOException e) {
            log.error("Error sending email: {}", email, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private Response sendMail(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        return sendGrid.api(request);
    }
}