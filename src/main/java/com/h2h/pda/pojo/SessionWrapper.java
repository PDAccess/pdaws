package com.h2h.pda.pojo;

import com.h2h.pda.entity.SessionEntity;

import java.sql.Timestamp;

public class SessionWrapper {
    private int sessionId;
    private String inventoryId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String externalSessionId;
    private String sessionType;
    private String username;
    private String ipAddress;
    String inventoryname;

    public SessionWrapper(SessionEntity sessionEntity) {
        setInventoryId(sessionEntity.getInventoryId());
        setStartTime(sessionEntity.getStartTime());
        setEndTime(sessionEntity.getEndTime());
        setUsername(sessionEntity.getUsername());
        setSessionId(sessionEntity.getSessionId());
        setSessionType(sessionEntity.getSessionType());
        setExternalSessionId(sessionEntity.getExternalSessionId());
    }

    public SessionWrapper(String inventoryname) {
        this.inventoryname = inventoryname;
    }

    public String getInventoryname() {
        return inventoryname;
    }

    public void setInventoryname(String inventoryname) {
        this.inventoryname = inventoryname;
    }

    public int getSessionId() {
        return sessionId;
    }

    public SessionWrapper setSessionId(int sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public SessionWrapper setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public SessionWrapper setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        return this;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public SessionWrapper setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    public SessionWrapper setExternalSessionId(String externalSessionId) {
        this.externalSessionId = externalSessionId;
        return this;
    }

    public String getSessionType() {
        return sessionType;
    }

    public SessionWrapper setSessionType(String sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SessionWrapper setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public SessionWrapper setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
}
