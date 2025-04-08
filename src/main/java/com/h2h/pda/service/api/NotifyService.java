package com.h2h.pda.service.api;

import com.h2h.pda.pojo.notification.NotificationType;

public interface NotifyService {
    void addNotification(String userId, String upperId, NotificationType type, boolean active);

    boolean hasActiveNotification(String userId, String upperId, NotificationType type);
}
