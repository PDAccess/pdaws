package com.h2h.pda.service.impl;

import com.h2h.pda.config.AsyncQueues;
import com.h2h.pda.pojo.SmsData;
import com.h2h.pda.service.api.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.Session;

@Service
public class SendSmsServiceImpl implements SendSmsService {

    private static final Logger log = LoggerFactory.getLogger(SendSmsServiceImpl.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @JmsListener(destination = AsyncQueues.QueueNames.SEND_SMS_QUEUE, containerFactory = "queueListenerFactory")
    public void sendEmail(@Payload SmsData data,
                          @Headers MessageHeaders headers,
                          Message message, Session session) {
        //@TODO: To be send sms for MFA
        log.info("Sms data came but not implemented: {}", data.toString());
    }


    @Override
    public void pushSMSRequest(SmsData data) {
        jmsTemplate.convertAndSend(AsyncQueues.QueueNames.SEND_SMS_QUEUE, data);
    }
}
