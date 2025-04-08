package com.h2h.pda.pojo;

import com.h2h.pda.entity.AlarmEntity;
import com.h2h.pda.entity.UserEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlarmWrapper implements EntityToDTO<AlarmWrapper, AlarmEntity> {
    private Integer alarmId;
    private String name;
    private String description;
    private String message;
    private boolean active;
    private Timestamp createdAt;
    private List<String> users = new ArrayList<>();
    private List<String> regex = new ArrayList<>();

    public AlarmWrapper() {
    }

    public AlarmWrapper(AlarmEntity alarmEntity) {
        wrap(alarmEntity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getRegex() {
        return regex;
    }

    public void setRegex(List<String> regex) {
        this.regex = regex;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public AlarmWrapper setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public AlarmWrapper setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public AlarmWrapper setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public AlarmWrapper wrap(AlarmEntity alarmEntity) {
        this.setName(alarmEntity.getName());
        this.setDescription(alarmEntity.getDescription());
        this.setMessage(alarmEntity.getMessage());
        this.setAlarmId(alarmEntity.getId());
        this.setCreatedAt(alarmEntity.getCreatedAt());
        this.setActive(alarmEntity.isActive());

        for (UserEntity userEntity : alarmEntity.getUserEntities()) {
            this.getUsers().add(userEntity.getUserId());
        }

        for (String regexEntity : alarmEntity.getAlarmRegexEntities()) {
            this.getRegex().add(regexEntity);
        }


        return this;
    }

    @Override
    public AlarmEntity unWrap() {
        AlarmEntity alarmEntity = new AlarmEntity();
        alarmEntity.setName(this.getName());
        alarmEntity.setDescription(this.getDescription());
        alarmEntity.setMessage(this.getMessage());
        alarmEntity.setId(this.getAlarmId());
        alarmEntity.setCreatedAt(this.getCreatedAt());
        alarmEntity.setActive(this.isActive());

        return alarmEntity;
    }
}
