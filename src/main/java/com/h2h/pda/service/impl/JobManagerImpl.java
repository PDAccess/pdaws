package com.h2h.pda.service.impl;

import com.h2h.pda.entity.LdapSynchronizationLogEntity;
import com.h2h.pda.repository.LdapSynchronizationLogRepository;
import com.h2h.pda.service.api.JobManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class JobManagerImpl implements JobManager {

    LdapSynchronizationLogRepository ldapSynchronizationLogRepository;

    @Override
    public void newJobRequest(LdapSynchronizationLogEntity ldapSynchronizationLogEntity) {
        ldapSynchronizationLogRepository.save(ldapSynchronizationLogEntity);
    }

    @Override
    public Iterable<LdapSynchronizationLogEntity> currentScheduleList(String groupId, String userId, PageRequest request) {
        return ldapSynchronizationLogRepository.findByGroupIdAndUser(groupId, userId, request);
    }
}
