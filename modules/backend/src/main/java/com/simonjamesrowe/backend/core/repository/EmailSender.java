package com.simonjamesrowe.backend.core.repository;

import com.simonjamesrowe.backend.core.model.Email;

public interface EmailSender {
    void sendEmail(Email email);
}