package com.h2h.pda.pojo;

import com.h2h.pda.entity.AutoCredantialSettingsEntity;

import java.sql.Timestamp;

public class AutoCredentialSettingsParams implements EntityToDTO<AutoCredentialSettingsParams, AutoCredantialSettingsEntity> {

    private Integer autoCredantialTime;
    private String autoCredantialTimeType;
    private Timestamp createdAt;
    private Timestamp lastAction;

    public Integer getAutoCredantialTime() {
        return autoCredantialTime;
    }

    public void setAutoCredantialTime(Integer autoCredantialTime) {
        this.autoCredantialTime = autoCredantialTime;
    }

    public String getAutoCredantialTimeType() {
        return autoCredantialTimeType;
    }

    public void setAutoCredantialTimeType(String autoCredantialTimeType) {
        this.autoCredantialTimeType = autoCredantialTimeType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastAction() {
        return lastAction;
    }

    public void setLastAction(Timestamp lastAction) {
        this.lastAction = lastAction;
    }

    @Override
    public AutoCredentialSettingsParams wrap(AutoCredantialSettingsEntity entity) {
        if (entity != null) {
            setAutoCredantialTime(entity.getAutoCredantialTime());
            setAutoCredantialTimeType(entity.getAutoCredantialTimeType());
            setCreatedAt(entity.getCreatedAt());
            setLastAction(entity.getLastAction());
            return this;
        }
        return null;
    }

    @Override
    public AutoCredantialSettingsEntity unWrap() {
        return null;
    }
}
