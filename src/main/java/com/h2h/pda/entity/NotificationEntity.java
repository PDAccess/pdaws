package com.h2h.pda.entity;

import com.h2h.pda.pojo.notification.NotificationType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "notifications")
public class NotificationEntity extends WhoCreateEntity {
    @Id
    private String id;

    @Column(name = "upper_id")
    private String upperId;
    @Column(name = "notify_type")
    private NotificationType type;
    private boolean active;

    public String getId() {
        return id;
    }

    public NotificationEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getUpperId() {
        return upperId;
    }

    public NotificationEntity setUpperId(String upperId) {
        this.upperId = upperId;
        return this;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationEntity setType(NotificationType type) {
        this.type = type;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public NotificationEntity setActive(boolean active) {
        this.active = active;
        return this;
    }
}
