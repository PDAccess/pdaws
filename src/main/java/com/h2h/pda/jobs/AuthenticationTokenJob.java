package com.h2h.pda.jobs;

import com.h2h.pda.entity.AuthenticationTokenEntity;
import com.h2h.pda.entity.JobHistoryEntity;
import com.h2h.pda.repository.AuthenticationTokenRepository;
import com.h2h.pda.service.api.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Configuration
@EnableScheduling
public class AuthenticationTokenJob {

    private static final int TIME_MINUTE_INTERVAL = 2;
    private static final String AUTHENTICATION_TOKEN_JOB = "Authentication Token Job";

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;

    @Autowired
    JobService jobService;

    @Scheduled(fixedDelay = 5000)
    public void executedTask() {

        JobHistoryEntity jobHistoryEntity = new JobHistoryEntity();
        jobHistoryEntity.setName(AUTHENTICATION_TOKEN_JOB);
        jobHistoryEntity.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));
        jobHistoryEntity = jobService.saveJob(jobHistoryEntity);

        try {
            List<AuthenticationTokenEntity> tokenEntities = authenticationTokenRepository.findAll();
            for (AuthenticationTokenEntity tokenEntity : tokenEntities) {
                Timestamp lastTokenAction = tokenEntity.getUpdatedAt();
                if (lastTokenAction != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(lastTokenAction);
                    calendar.add(Calendar.MINUTE, TIME_MINUTE_INTERVAL);

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime endTokenAction = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
                    if (now.isAfter(endTokenAction)) {
                        authenticationTokenRepository.delete(tokenEntity);
                    }
                } else {
                    authenticationTokenRepository.delete(tokenEntity);
                }
            }
            jobHistoryEntity.setSuccess(true);
        } catch (Exception exception) {
            jobHistoryEntity.setDescription(exception.getMessage());
            jobHistoryEntity.setSuccess(false);
        } finally {
            jobHistoryEntity.setFinishedAt(Timestamp.valueOf(LocalDateTime.now()));
            jobService.saveJob(jobHistoryEntity);
        }
    }

}
