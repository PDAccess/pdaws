package com.h2h.pda.pojo;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.service.ServiceType;

public class CredentialManagerWrapper {

    private String serviceId;
    private String token;
    private String ipAddress;
    private Integer port;
    private Integer userId;
    private ServiceType type;
    private String credentialId;
    private String password;
    private UserEntity whoTriggered;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserEntity getWhoTriggered() {
        return whoTriggered;
    }

    public void setWhoTriggered(UserEntity whoTriggered) {
        this.whoTriggered = whoTriggered;
    }
}
