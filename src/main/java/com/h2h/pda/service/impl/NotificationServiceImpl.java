package com.h2h.pda.service.impl;

import com.h2h.pda.entity.NotificationEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.notification.NotificationType;
import com.h2h.pda.repository.NotificationRepo;
import com.h2h.pda.service.api.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotifyService {
    @Autowired
    NotificationRepo notificationRepo;

    @Override
    public void addNotification(String userId, String upperId, NotificationType type, boolean active) {
        Iterable<NotificationEntity> notificationEntities = notificationRepo.getNotifications(userId, upperId, type);
        NotificationEntity entity;
        if (notificationEntities != null && notificationEntities.iterator().hasNext()) {
            entity = notificationEntities.iterator().next();
        } else {
            entity = new NotificationEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setWhoCreate(new UserEntity().setUserId(userId));
            entity.setUpperId(upperId);
            entity.setType(type);
        }

        entity.setActive(active);

        notificationRepo.save(entity);
    }

    @Override
    public boolean hasActiveNotification(String userId, String upperId, NotificationType type) {
        Iterable<NotificationEntity> entities = notificationRepo.checkNotification(userId, upperId, type, true);
        return entities != null && entities.iterator().hasNext();
    }
}
