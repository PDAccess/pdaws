package com.h2h.pda.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "auto_credantial_settings")
public class AutoCredantialSettingsEntity {

    @Id
    private String credantialId;
    private Integer autoCredantialTime;
    private String autoCredantialTimeType;
    private Timestamp createdAt;
    private Timestamp lastAction;

    @OneToOne(mappedBy = "autoCredantialSettingsEntity")
    private CredentialEntity credentialEntity;

    public String getCredantialId() {
        return credantialId;
    }

    public void setCredantialId(String credantialId) {
        this.credantialId = credantialId;
    }

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

    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
    }
}
