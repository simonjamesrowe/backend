package com.simonjamesrowe.apigateway.entrypoints.restcontroller;

import com.simonjamesrowe.apigateway.core.model.ContactUsRequest;
import com.simonjamesrowe.apigateway.core.usecase.ContactUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContactUsController {

    private final ContactUseCase contactUseCase;

    @PostMapping("/contact-us")
    public ResponseEntity<Void> contactUs(
            @RequestBody @Valid ContactUs contactUs,
            @RequestHeader(value = "Referer", required = false) String referer) {

        log.info("Contact Us request made: {}", contactUs);

        ContactUsRequest request = new ContactUsRequest(
                contactUs.getFirstName(),
                contactUs.getLastName(),
                contactUs.getEmailAddress(),
                contactUs.getSubject(),
                contactUs.getContent(),
                referer
        );

        contactUseCase.contactUs(request);
        return ResponseEntity.ok().build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactUs {
        @NotBlank
        private String firstName;
        
        @NotBlank
        private String lastName;
        
        @NotBlank
        @Email
        private String emailAddress;
        
        @NotBlank
        private String subject;
        
        @NotBlank
        private String content;
    }
}