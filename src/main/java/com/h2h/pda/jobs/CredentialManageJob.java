package com.h2h.pda.jobs;

import com.h2h.pda.entity.AutoCredentialsHistoryEntity;
import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.ServiceEntity;
import com.h2h.pda.pojo.CredentialManagerWrapper;
import com.h2h.pda.service.api.CredentialManager;
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
public class CredentialManageJob {

    public static final int CHANGE_INTERVAL_DAY = 7;

    @Autowired
    CredentialManager credentialManager;

    @Scheduled(fixedDelay = 60000)
    public void manageCredentials() {
        List<CredentialEntity> credentialEntityList = credentialManager.getManagedCredentials();
        for (CredentialEntity credentialEntity:credentialEntityList) {
            AutoCredentialsHistoryEntity autoCredentialsHistoryEntity = credentialManager.getLastCredentialChange(credentialEntity.getCredentialId());
            if (autoCredentialsHistoryEntity != null && autoCredentialsHistoryEntity.getResult() && autoCredentialsHistoryEntity.getEndAt() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(autoCredentialsHistoryEntity.getEndAt());
                if (credentialEntity.getCredentialManageTime() != null) cal.add(Calendar.SECOND, credentialEntity.getCredentialManageTime());
                else cal.add(Calendar.DATE, CHANGE_INTERVAL_DAY);
                Timestamp lastChangeTimeout = new Timestamp(cal.getTime().getTime());
                Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
                if (currentTime.after(lastChangeTimeout)) {
                    ServiceEntity serviceEntity = credentialEntity.getConnectionUser().getServiceEntity();

                    CredentialManagerWrapper data = new CredentialManagerWrapper();
                    data.setType(serviceEntity.getServiceTypeId());
                    data.setServiceId(serviceEntity.getInventoryId());
                    data.setPort(serviceEntity.getPort());
                    data.setIpAddress(serviceEntity.getIpAddress());
                    data.setUserId(credentialEntity.getConnectionUser().getId());
                    data.setCredentialId(credentialEntity.getCredentialId());
                    credentialManager.pushChangeRequest(data);
                }
            }
        }
    }

}
