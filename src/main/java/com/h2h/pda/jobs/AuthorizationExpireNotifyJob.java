package com.h2h.pda.jobs;

import com.h2h.pda.entity.GroupUserEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.service.api.GroupOps;
import com.h2h.pda.service.api.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class AuthorizationExpireNotifyJob {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationExpireNotifyJob.class);

    private static final int DAYS_LEFT_FOR_EXPIRE_FIRST = 7;
    private static final int DAYS_LEFT_FOR_EXPIRE_LAST = 4;

    @Autowired
    GroupOps groupOps;

    @Autowired
    SendEmailService sendEmailService;

    @Scheduled(fixedDelay = 900000)
    public void executedTask() {
        List<GroupUserEntity> groupUserEntityList = groupOps.getGroupMemberByExpire(DAYS_LEFT_FOR_EXPIRE_FIRST);
        for (GroupUserEntity groupUserEntity : groupUserEntityList) {

            GroupsEntity groupsEntity = groupUserEntity.getGroup();
            UserEntity userEntity = groupUserEntity.getUser();

            EmailData emailData = new EmailData();
            emailData.setToMail(groupUserEntity.getUser().getEmail());
            emailData.setSubject("PDAccess Group Authority");
            emailData.setText(String.format("%d days until your %s User's Authorization for the PDAccess %s Group is invalid.", DAYS_LEFT_FOR_EXPIRE_FIRST, userEntity.getUsername(), groupsEntity.getGroupName()));
            sendEmailService.pushEmailRequest(emailData);
        }

        groupUserEntityList = groupOps.getGroupMemberByExpire(DAYS_LEFT_FOR_EXPIRE_LAST);
        for (GroupUserEntity groupUserEntity : groupUserEntityList) {

            GroupsEntity groupsEntity = groupUserEntity.getGroup();
            UserEntity userEntity = groupUserEntity.getUser();

            EmailData emailData = new EmailData();
            emailData.setToMail(groupUserEntity.getUser().getEmail());
            emailData.setSubject("PDAccess Group Authority");
            emailData.setText(String.format("%d days until your %s User's Authorization for the PDAccess %s Group is invalid.", DAYS_LEFT_FOR_EXPIRE_LAST, userEntity.getUsername(), groupsEntity.getGroupName()));
            sendEmailService.pushEmailRequest(emailData);
        }
    }

}
