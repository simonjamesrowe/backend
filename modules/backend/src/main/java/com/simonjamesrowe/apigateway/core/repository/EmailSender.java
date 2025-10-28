package com.simonjamesrowe.apigateway.core.repository;

import com.simonjamesrowe.apigateway.core.model.Email;

public interface EmailSender {
    void sendEmail(Email email);
}