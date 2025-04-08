package com.h2h.pda.service.api;

import com.h2h.pda.pojo.mail.EmailData;

public interface SendEmailService {
    void pushEmailRequest(EmailData emailData);
}
