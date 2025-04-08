package com.h2h.pda.jobs;

import com.h2h.pda.entity.*;
import com.h2h.pda.pojo.CredentialManagerWrapper;
import com.h2h.pda.repository.AutoCredentialHistoryRepository;
import com.h2h.pda.repository.AutoCredentialSettingsRepository;
import com.h2h.pda.service.api.CredentialManager;
import com.h2h.pda.service.api.JobService;
import com.h2h.pda.service.api.MetricService;
import com.h2h.pda.service.api.ServiceOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.vault.VaultException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.h2h.pda.pojo.metric.Counters.PASSWORD_JOB_COUNTS;

@Configuration
@EnableScheduling
public class AutoCredentialScheduler {

    private static final Logger log = LoggerFactory.getLogger(AutoCredentialScheduler.class);
    private static final String AUTO_CREDENTIAL_SCHEDULER_JOB = "Automatic Credential Scheduler Job";

    @Autowired
    AutoCredentialSettingsRepository repo;

    @Autowired
    AutoCredentialHistoryRepository historyRepository;

    @Autowired
    MetricService metricService;

    @Autowired
    JobService jobService;

    @Autowired
    ServiceOps serviceOps;

    @Autowired
    CredentialManager credentialManager;

    @Scheduled(fixedDelay = 900000)
    public void executedTask() {

        JobHistoryEntity jobHistoryEntity = new JobHistoryEntity();
        jobHistoryEntity.setName(AUTO_CREDENTIAL_SCHEDULER_JOB);
        jobHistoryEntity.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));
        jobHistoryEntity = jobService.saveJob(jobHistoryEntity);

        try {
            log.info("{} Task executed. {}", Thread.currentThread().getName(), new Date());
            List<AutoCredantialSettingsEntity> autoCredentialSettingsEntities = repo.findAll();

            metricService.getCounter(PASSWORD_JOB_COUNTS).increment();

            for (AutoCredantialSettingsEntity autoCredantialSettingsEntity : autoCredentialSettingsEntities) {
                Timestamp lastActionDb = autoCredantialSettingsEntity.getLastAction();
                Integer time = autoCredantialSettingsEntity.getAutoCredantialTime();
                Calendar cal = Calendar.getInstance();
                if (lastActionDb != null) {
                    cal.setTime(lastActionDb);
                } else {
                    autoCredantialSettingsEntity.setLastAction(new Timestamp(System.currentTimeMillis()));
                    repo.save(autoCredantialSettingsEntity);
                }

                cal.add(Calendar.SECOND, time);

                LocalDateTime now = LocalDateTime.now();

                LocalDateTime lastAction = LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());

                if (now.isAfter(lastAction)) {
                    now = LocalDateTime.now();

                    CredentialEntity credentialEntity = autoCredantialSettingsEntity.getCredentialEntity();

                    if (credentialEntity != null && credentialEntity.getConnectionUser() != null && credentialEntity.getConnectionUser().getServiceEntity() != null) {
                        ServiceEntity serviceEntity = credentialEntity.getConnectionUser().getServiceEntity();
                        try {

                            CredentialManagerWrapper data = new CredentialManagerWrapper();
                            data.setType(serviceEntity.getServiceTypeId());
                            data.setServiceId(serviceEntity.getInventoryId());
                            data.setPort(serviceEntity.getPort());
                            data.setIpAddress(serviceEntity.getIpAddress());
                            data.setUserId(credentialEntity.getConnectionUser().getId());
                            data.setCredentialId(credentialEntity.getCredentialId());
                            credentialManager.pushChangeRequest(data);
                        } catch (VaultException ve) {
                            log.error("Authentication error", ve);
                            AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = new AutoCredentialsHistoryEntity();
                            autoCredentialsHistoryEntity.setId(UUID.randomUUID().toString());
                            autoCredentialsHistoryEntity.setCredentialId(credentialEntity.getCredentialId());
                            autoCredentialsHistoryEntity.setCredentialId(credentialEntity.getCredentialId());
                            autoCredentialsHistoryEntity.setEndAt(Timestamp.valueOf(now));
                            autoCredentialsHistoryEntity.setResult(false);
                            autoCredentialsHistoryEntity.setDescription(ve.getMessage());
                            historyRepository.save(autoCredentialsHistoryEntity);
                        }
                    } else {
                        //service not found
                        AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = new AutoCredentialsHistoryEntity();
                        autoCredentialsHistoryEntity.setId(UUID.randomUUID().toString());
                        autoCredentialsHistoryEntity.setEndAt(Timestamp.valueOf(now));
                        autoCredentialsHistoryEntity.setResult(false);
                        autoCredentialsHistoryEntity.setDescription("Credential or Local account not found!");
                        historyRepository.save(autoCredentialsHistoryEntity);
                    }

                    autoCredantialSettingsEntity.setLastAction(Timestamp.valueOf(LocalDateTime.now()));
                    repo.save(autoCredantialSettingsEntity);
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


