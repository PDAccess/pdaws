package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.pojo.session.SessionType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "alarm_histories")
public class AlarmHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alarmHistoriesSequenceGenerator")
    @SequenceGenerator(name = "alarmHistoriesSequenceGenerator", sequenceName = "alarms_histories_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", referencedColumnName = "id")
    private AlarmEntity alarmEntity;

    @Column(name = "user_name")
    private String username;

    private Timestamp createdAt;

    @JsonProperty("executed_at")
    private Long executedAt;

    @JsonProperty("service_id")
    private String serviceId;

    @JsonProperty("session_id")
    private Long sessionId;

    @Column(name = "session_type")
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AlarmEntity getAlarmEntity() {
        return alarmEntity;
    }

    public void setAlarmEntity(AlarmEntity alarmEntity) {
        this.alarmEntity = alarmEntity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public Long getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Long executedAt) {
        this.executedAt = executedAt;
    }

}
