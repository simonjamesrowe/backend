package com.simonjamesrowe.apigateway.core.usecase;

import com.simonjamesrowe.apigateway.core.model.ContactUsRequest;
import com.simonjamesrowe.apigateway.core.model.Email;
import com.simonjamesrowe.apigateway.core.repository.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactUseCase {

    private final EmailSender emailSender;

    public void contactUs(ContactUsRequest contactUsRequest) {
        Email email = new Email(
                contactUsRequest.getSubject(),
                "text/plain",
                textPlainBody(contactUsRequest)
        );
        emailSender.sendEmail(email);
    }

    private String textPlainBody(ContactUsRequest contactUsRequest) {
        return String.format("""
                A message has been sent from the site: %s
                Email Address: %s
                Name: %s %s
                Content: %s
                """,
                contactUsRequest.getReferrer(),
                contactUsRequest.getEmail(),
                contactUsRequest.getFirst(),
                contactUsRequest.getLast(),
                contactUsRequest.getMessage()).trim();
    }
}