package com.h2h.pda.service.api;

import com.h2h.pda.pojo.SmsData;

public interface SendSmsService {
    void pushSMSRequest(SmsData data);
}
