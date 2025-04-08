package com.h2h.pda.repository;

import com.h2h.pda.entity.NotificationEntity;
import com.h2h.pda.pojo.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationEntity, String> {

    @Query(value = "SELECT n FROM NotificationEntity n WHERE n.whoCreate.userId = :userId and n.upperId = :upperId and n.type = :type and active IS :isActive")
    Iterable<NotificationEntity> checkNotification(@Param("userId") String userId, @Param("upperId") String upperId, @Param("type") NotificationType type, @Param("isActive") boolean isActive);

    @Query(value = "SELECT n FROM NotificationEntity n WHERE n.whoCreate.userId = :userId and n.upperId = :upperId and n.type = :type")
    Iterable<NotificationEntity> getNotifications(@Param("userId") String userId, @Param("upperId") String upperId, @Param("type") NotificationType type);
}
