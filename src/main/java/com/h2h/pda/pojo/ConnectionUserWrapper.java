package com.h2h.pda.pojo;

import com.h2h.pda.entity.*;

import java.sql.Timestamp;
import java.util.List;

// @TODO: Entity Fix
public class ConnectionUserWrapper implements EntityToDTO<ConnectionUserWrapper, ConnectionUserEntity> {

    private int id;
    private String username;
    private ServiceEntity serviceEntity;
    private List<GroupUserEntity> groupUserEntities;
    private List<BreakTheGlassEntity> breaktheglassEntities;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isAdmin;
    private Boolean deletable;
    private List<CredentialEntity> credentials;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public List<GroupUserEntity> getGroupUserEntities() {
        return groupUserEntities;
    }

    public void setGroupUserEntities(List<GroupUserEntity> groupUserEntities) {
        this.groupUserEntities = groupUserEntities;
    }

    public List<BreakTheGlassEntity> getBreaktheglassEntities() {
        return breaktheglassEntities;
    }

    public void setBreaktheglassEntities(List<BreakTheGlassEntity> breaktheglassEntities) {
        this.breaktheglassEntities = breaktheglassEntities;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public List<CredentialEntity> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<CredentialEntity> credentials) {
        this.credentials = credentials;
    }

    @Override
    public ConnectionUserWrapper wrap(ConnectionUserEntity entity) {
        if (entity != null) {
            setId(entity.getId());
            setUsername(entity.getUsername());
            setCreatedAt(entity.getCreatedAt());
            setUpdatedAt(entity.getUpdatedAt());
            setAdmin(entity.getAdmin());
        }
        return this;
    }

    @Override
    public ConnectionUserEntity unWrap() {
        return null;
    }
}
