package com.h2h.pda.pojo.alarm;

import com.h2h.pda.entity.AlarmEntity;
import com.h2h.pda.entity.AlarmHistoryEntity;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.pojo.EntityToDTO;
import com.h2h.pda.pojo.session.SessionType;

import java.sql.Timestamp;
import java.util.Set;

public class AlarmHistoryResponse implements EntityToDTO<AlarmHistoryResponse, AlarmHistoryEntity> {

    private long id;
    private String username;
    private String group;
    private String groupId;
    private String serviceName;
    private String serviceId;
    private Long sessionId;
    private String policyName;
    private Integer policyId;
    private Long executedAt;
    private SessionType sessionType;
    private String description;
    private String message;
    private Set<String> regex;
    private Timestamp createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public AlarmHistoryResponse wrap(AlarmHistoryEntity entity) {
        setId(entity.getId());
        setUsername(entity.getUsername());
        setCreatedAt(entity.getCreatedAt());
        setServiceId(entity.getServiceId());
        setSessionId(entity.getSessionId());
        setExecutedAt(entity.getExecutedAt());
        setSessionType(entity.getSessionType());

        AlarmEntity alarmEntity = entity.getAlarmEntity();
        if (alarmEntity != null) {
            setDescription(alarmEntity.getDescription());
            setMessage(alarmEntity.getMessage());
            setRegex(alarmEntity.getAlarmRegexEntities());
            setPolicyId(alarmEntity.getId());
            setPolicyName(alarmEntity.getName());
            GroupsEntity groupsEntity = alarmEntity.getGroupsEntity();
            if (groupsEntity != null) {
                setGroup(groupsEntity.getGroupName());
                setGroupId(groupsEntity.getGroupId());
            }
        }

        return this;
    }

    @Override
    public AlarmHistoryEntity unWrap() {
        return null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getRegex() {
        return regex;
    }

    public void setRegex(Set<String> regex) {
        this.regex = regex;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public Long getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Long executedAt) {
        this.executedAt = executedAt;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
