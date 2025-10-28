package com.simonjamesrowe.apigateway.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRequest {
    private String first;
    private String last;
    private String email;
    private String subject;
    private String message;
    private String referrer;
}