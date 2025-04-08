package com.h2h.pda.service.impl;

import com.h2h.pda.entity.DeletableBaseEntity;
import com.h2h.pda.pojo.LogEntityAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class ActionServiceImpl {
    private Logger log = LoggerFactory.getLogger(ActionServiceImpl.class);

    @JmsListener(destination = "multicast://pda.#", containerFactory = "topicConnectionFactory")
    void logPDAEvents(final Message message) {
        log.info("Message came: {}", message.getHeaders().get(JmsHeaders.DESTINATION));
        Object payload = message.getPayload();
        if (payload instanceof LogEntityAction) {
            LogEntityAction lea = (LogEntityAction) message.getPayload();
            log.info("Event Logged {} {}", message.getHeaders().get(JmsHeaders.DESTINATION), lea.label());
        }

        if (payload instanceof DeletableBaseEntity) {
            DeletableBaseEntity dbe = (DeletableBaseEntity) message.getPayload();
            log.info("PDAccess Event Created {} {} {}", message.getHeaders().get(JmsHeaders.DESTINATION), dbe.getCreatedAt(), payload.getClass());
        }
    }
}
