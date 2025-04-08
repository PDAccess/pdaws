package com.h2h.pda.service.impl;

import com.h2h.pda.entity.SessionEntity;
import com.h2h.pda.pojo.service.ServiceMeta;
import com.h2h.pda.repository.SessionRepository;
import com.h2h.pda.service.api.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    SessionRepository sessionRepository;

    @Override
    public Integer start(String username, ServiceMeta meta, String serviceId, String ipAddress) {
        return start(username, meta, serviceId, ipAddress, UUID.randomUUID().toString());
    }

    @Override
    public Integer start(String username, ServiceMeta meta, String serviceId, String ipAddress, String externalSessionId) {
        SessionEntity session = new SessionEntity();
        session.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        session.setUsername(username);
        session.setSessionType(meta.getPropertyString());
        session.setInventoryId(serviceId);
        session.setExternalSessionId(externalSessionId);
        session.setIpAddress(ipAddress);

        session = sessionRepository.save(session);

        return session.getSessionId();
    }
}
