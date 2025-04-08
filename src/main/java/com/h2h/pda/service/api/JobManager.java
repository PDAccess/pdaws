package com.h2h.pda.service.api;

import com.h2h.pda.entity.LdapSynchronizationLogEntity;
import org.springframework.data.domain.PageRequest;

public interface JobManager {
    void newJobRequest(LdapSynchronizationLogEntity ldapSynchronizationLogEntity);

    Iterable<LdapSynchronizationLogEntity> currentScheduleList(String groupId, String userId, PageRequest request);
}
