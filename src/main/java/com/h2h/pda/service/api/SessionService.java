package com.h2h.pda.service.api;

import com.h2h.pda.pojo.service.ServiceMeta;

public interface SessionService {
    Integer start(String username, ServiceMeta meta, String serviceId, String ipAddress);

    Integer start(String username, ServiceMeta meta, String serviceId, String ipAddress, String externalSessionId);
}
