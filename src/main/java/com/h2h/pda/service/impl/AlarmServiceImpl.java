package com.h2h.pda.service.impl;

import com.h2h.pda.config.AsyncQueues;
import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.ActionPayload;
import com.h2h.pda.pojo.Pagination;
import com.h2h.pda.pojo.group.GroupRole;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.repository.ActionRepository;
import com.h2h.pda.repository.AlarmHistoryRepository;
import com.h2h.pda.repository.AlarmRepository;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.Session;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.h2h.pda.config.AsyncQueues.QueueNames.SEND_EMAIL_QUEUE;
import static com.h2h.pda.pojo.metric.Counters.ALARM_COUNTS;

@Service
public class AlarmServiceImpl implements AlarmService {
    String PDACCESS_WARNING = "PDAccess warning";
    String MESSAGE = "message";

    private Logger log = LoggerFactory.getLogger(AlarmServiceImpl.class);

    @Autowired
    AlarmRepository alarmRepository;

    @Autowired
    AlarmHistoryRepository alarmHistoryRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    MetricService metricService;

    @Autowired
    GroupOps groupOps;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    UsersOps usersOps;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = AsyncQueues.QueueNames.ACTION_QUEUE, containerFactory = "queueListenerFactory")
    public void actionListener(@Payload ActionPayload actionPayload,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {

        SessionEntity sessionEntity = sessionRepository.findBySessionId(actionPayload.getSessionId());
        ActionEntity actionEntity = new ActionEntity(actionPayload.getProxyAction(), sessionEntity);
        actionEntity.setActionTime(actionPayload.getActionTime());
        actionEntity.setSessionId(actionPayload.getSessionId());
        actionRepository.save(actionEntity);

        String commands = actionPayload.getProxyAction();
        String command;
        String params;

        int splitIndex = commands.indexOf(' ');
        if (splitIndex != -1) {
            command = commands.substring(0, splitIndex);
            params = commands.substring(splitIndex + 1);
        } else {
            command = commands;
            params = "";
        }

        List<GroupsEntity> groupIds = serviceOps.effectiveGroups(sessionEntity.getServiceEntity().getInventoryId());

        String user = actionPayload.getUsername();
        String time = String.valueOf(actionPayload.getActionTime().getTime());
        String host = "PDAccess Client";
        String serviceId = sessionEntity.getServiceEntity().getInventoryId();

        execTraceServiceAlarm(command, params, user, time, host, serviceId);

        for (GroupsEntity group : groupIds) {
            execTraceGroupAlarm(command, params, user, time, host, group.getGroupId());
        }

    }

    public void execTraceServiceAlarm(String command, String param, String user, String time, String host, String serviceId) {
        List<AlarmEntity> alarmEntities = alarmRepository.findAllByActiveAndService(serviceId);
        sendTraceMail(command, param, user, time, host, alarmEntities);
    }

    public void execTraceGroupAlarm(String command, String param, String user, String time, String host, String groupId) {
        List<AlarmEntity> alarmEntities = alarmRepository.findAllByGroup(groupId);
        sendTraceMail(command, param, user, time, host, alarmEntities);
    }

    public void sendTraceMail(String command, String param, String user, String time, String host, List<AlarmEntity> alarms) {
        command = command.toLowerCase();
        param = param.toLowerCase();

        for (AlarmEntity alarmEntity:alarms) {
            String message = alarmEntity.getMessage();
            if (alarmEntity.isActive()) {
                for (String regexEntity : alarmEntity.getAlarmRegexEntities()) {
                    String regex = regexEntity.toLowerCase();
                    if (command.contains(regex) || param.contains(regex)) {

                        metricService.getCounter(ALARM_COUNTS).increment();

                        EmailData emailData = new EmailData();
                        emailData.setMailName(EmailNames.EX_TRACE_ACTION);
                        emailData.setSubject(PDACCESS_WARNING);
                        emailData.getHtml().put("user", user);
                        emailData.getHtml().put("command", command);
                        emailData.getHtml().put(MESSAGE, message);
                        emailData.getHtml().put("param", param);
                        emailData.getHtml().put("host", host);
                        try {
                            emailData.getHtml().put("time", LocalDateTime.ofEpochSecond(Long.parseLong(time), 0, ZoneOffset.ofHours(3)).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (NumberFormatException e) {
                            log.warn("Cannot format date {}", time);
                        }

                        StringBuilder users = new StringBuilder();
                        for (UserEntity userEntity : alarmEntity.getUserEntities()) {
                            if (userEntity.getDeletedAt() == null) {
                                users.append(userEntity.getEmail());
                                users.append(",");
                            }
                        }

                        if (users.length() != 0) {
                            emailData.setToMail(users.toString());
                            jmsTemplate.convertAndSend(SEND_EMAIL_QUEUE, emailData);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void pushAction(ActionPayload actionPayload) {
        jmsTemplate.convertAndSend(AsyncQueues.QueueNames.ACTION_QUEUE, actionPayload);
    }

    @Override
    public Optional<AlarmEntity> byId(Integer alarmId) {
        return alarmRepository.findById(alarmId);
    }

    @Override
    public List<AlarmEntity> byServiceId(String serviceId) {
        return alarmRepository.findAllByActiveAndService(serviceId);
    }

    @Override
    public List<AlarmEntity> byGroupId(String groupId) {
        return alarmRepository.findAllByGroup(groupId);
    }

    @Override
    public long getAlarmCount() {
        return alarmHistoryRepository.countByGroupMembership(usersOps.securedUser().getUserId(), GroupRole.ADMIN);
    }

    @Override
    public List<AlarmHistoryEntity> getAlarmHistories(Pagination pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getCurrentPage(), pagination.getPerPage(), Sort.by("id").descending());
        return alarmHistoryRepository.findAllByGroupMembership(pagination.getFilter(), usersOps.securedUser().getUserId(), GroupRole.ADMIN, pageRequest);
    }
}
