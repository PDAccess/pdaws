package com.h2h.pda.pojo;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.SessionEntity;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;

import java.sql.Timestamp;

public class SessionEntityWrapper implements EntityToDTO<SessionEntityWrapper, SessionEntity> {

    private int sessionId;
    private String inventoryId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String externalSessionId;
    private String sessionType;
    private String username;
    private UserDTO userEntity;
    private ServiceEntityWrapper serviceEntity;
    private String ipAddress;
    private String credentialId;
    private String credentialUsername;

    public SessionEntityWrapper() {
    }

    public SessionEntityWrapper(SessionEntity sessionEntity) {
        wrap(sessionEntity);
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    public void setExternalSessionId(String externalSessionId) {
        this.externalSessionId = externalSessionId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp starttime) {
        this.startTime = starttime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserDTO getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserDTO userEntity) {
        this.userEntity = userEntity;
    }

    public ServiceEntityWrapper getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntityWrapper serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public SessionEntityWrapper setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public SessionEntityWrapper wrap(SessionEntity entity) {
        setSessionId(entity.getSessionId());
        setServiceEntity(new ServiceEntityWrapper(entity.getServiceEntity()));
        setInventoryId(entity.getInventoryId());
        setIpAddress(entity.getIpAddress());
        setUserEntity(new UserDTO(entity.getUserEntity()));
        setEndTime(entity.getEndTime());
        setStartTime(entity.getStartTime());

        if (entity.getCredentialEntity() != null) {
            CredentialEntity credentialEntity = entity.getCredentialEntity();
            setCredentialId(credentialEntity.getCredentialId());
            setCredentialUsername(credentialEntity.getUsername());
        }

        return this;
    }

    @Override
    public SessionEntity unWrap() {
        SessionEntity entity = new SessionEntity();
        entity.setSessionId(getSessionId());
        return entity;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialUsername() {
        return credentialUsername;
    }

    public void setCredentialUsername(String credentialUsername) {
        this.credentialUsername = credentialUsername;
    }
}
