package com.h2h.pda.service.impl;

import com.h2h.pda.entity.ActionEntity;
import com.h2h.pda.jwt.TokenDetails;
import com.h2h.pda.pojo.ActionPdaData;
import com.h2h.pda.repository.ActionRepository;
import com.h2h.pda.service.api.ActionPdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.jms.Session;
import java.sql.Timestamp;

import static com.h2h.pda.config.AsyncQueues.QueueNames.ACTION_PDA_QUEUE;

@Service
public class ActionPdaServiceImpl implements ActionPdaService {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    JmsTemplate jmsTemplate;

    @JmsListener(destination = ACTION_PDA_QUEUE, containerFactory = "queueListenerFactory")
    public void saveActionPda(@Payload ActionPdaData data,
                              @Headers MessageHeaders headers,
                              Message message, Session session) {
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setSessionId(data.getSessionId());
        actionEntity.setActionTime(data.getActionTime());
        actionEntity.setProxyAction(data.getActionMessage());
        actionRepository.save(actionEntity);
    }

    @Override
    public void saveAction(ActionPdaData data) {
        jmsTemplate.convertAndSend(ACTION_PDA_QUEUE, data);
    }

    @Override
    public void saveAction(String actionPayload) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        TokenDetails tokenDetails = (TokenDetails) authentication.getDetails();


        ActionPdaData actionPdaData = new ActionPdaData();
        actionPdaData.setSessionId(0);
        actionPdaData.setActionTime(new Timestamp(System.currentTimeMillis()));
        actionPdaData.setActionMessage(actionPayload);
        jmsTemplate.convertAndSend(ACTION_PDA_QUEUE, actionPdaData);
    }

    @Override
    public void saveAction(String actionPayload, int sessionId) {
        ActionPdaData actionPdaData = new ActionPdaData();
        actionPdaData.setSessionId(sessionId);
        actionPdaData.setActionTime(new Timestamp(System.currentTimeMillis()));
        actionPdaData.setActionMessage(actionPayload);
        jmsTemplate.convertAndSend(ACTION_PDA_QUEUE, actionPdaData);
    }
}