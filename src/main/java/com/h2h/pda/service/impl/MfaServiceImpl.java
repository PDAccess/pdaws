package com.h2h.pda.service.impl;

import com.h2h.pda.entity.AuthenticationTokenEntity;
import com.h2h.pda.entity.MfaEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.MfaParams;
import com.h2h.pda.pojo.SmsData;
import com.h2h.pda.pojo.UserDTO;
import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.repository.AuthenticationTokenRepository;
import com.h2h.pda.repository.MfaRepository;
import com.h2h.pda.service.api.MfaService;
import com.h2h.pda.service.api.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.jms.Session;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.h2h.pda.config.AsyncQueues.QueueNames.SEND_EMAIL_QUEUE;
import static com.h2h.pda.config.AsyncQueues.QueueNames.SEND_SMS_QUEUE;

@Service
public class MfaServiceImpl implements MfaService {

    @Autowired
    MfaRepository mfaRepository;

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    JmsTemplate jmsTemplate;

    @Override
    public void sendMfaCode(UserEntity userEntity, String ipAddress) {
        MfaParams mfaParams = new MfaParams(userEntity, generateMfaCode(userEntity.getUsername()), ipAddress);
        jmsTemplate.convertAndSend(QUEUE, mfaParams);
    }

    @Override
    public boolean checkMfaCode(String username, String mfaCode) {
        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (optionalMfaEntity.isPresent()) {
            MfaEntity mfaEntity = optionalMfaEntity.get();
            try {
                String code = tokenGenerator.totp(mfaEntity.getSecretKey());
                return mfaCode.equals(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String generateMfaCode(String username) {
        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(username);
        if (optionalMfaEntity.isPresent()) {
            MfaEntity mfaEntity = optionalMfaEntity.get();
            try {
                return tokenGenerator.totp(mfaEntity.getSecretKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void storeToken(String username, String token) {
        AuthenticationTokenEntity authenticationTokenEntity = authenticationTokenRepository.findByUsername(username);
        if (authenticationTokenEntity == null) {
            authenticationTokenEntity = new AuthenticationTokenEntity();
            authenticationTokenEntity.setUsername(username);
            authenticationTokenEntity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        authenticationTokenEntity.setToken(token);
        authenticationTokenEntity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        authenticationTokenRepository.save(authenticationTokenEntity);
    }

    @Override
    public Optional<MfaEntity> getMfaStatusByUsername(String username) {
        return mfaRepository.findByUsername(username);
    }

    @JmsListener(destination = QUEUE, containerFactory = "queueListenerFactory")
    public void sendMfaCode(@Payload MfaParams mfaParams,
                            @Headers MessageHeaders headers,
                            Message message, Session session) {

        UserDTO userEntity = mfaParams.getUserEntity();
        String mfaCode = mfaParams.getMfaCode();
        if (userEntity == null || StringUtils.hasText(mfaCode)) {
            return;
        }

        Optional<MfaEntity> optionalMfaEntity = mfaRepository.findByUsername(userEntity.getUsername());
        if (!optionalMfaEntity.isPresent()) {
            return;
        }

        MfaEntity mfaEntity = optionalMfaEntity.get();

        if (Boolean.TRUE.equals(mfaEntity.getSms())) {
            //Sending sms
            SmsData smsData = new SmsData();
            smsData.setToPhone(userEntity.getEmail());
            smsData.setMessage(String.format("OTP Code is: %s", mfaCode));
            jmsTemplate.convertAndSend(SEND_SMS_QUEUE, smsData);
        }

        if (Boolean.TRUE.equals(mfaEntity.getEmail())) {
            EmailData emailData = new EmailData();
            emailData.setToMail(userEntity.getEmail());
            emailData.setSubject(String.format("MFA Authentication: %s %s ", userEntity.getUsername(), mfaParams.getIpAddress()));
            emailData.setText(mfaCode);
            emailData.setMailName(EmailNames.MFA_MAIL);
            jmsTemplate.convertAndSend(SEND_EMAIL_QUEUE, emailData);
        }
    }

}
