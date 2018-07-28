package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.model.Mail;

public interface EmailService {
    void sendEmail(Mail mail);
}
