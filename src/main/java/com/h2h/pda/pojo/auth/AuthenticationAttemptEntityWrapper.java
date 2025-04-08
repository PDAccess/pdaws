package com.h2h.pda.pojo.auth;

import com.h2h.pda.entity.AuthenticationAttemptEntity;
import com.h2h.pda.pojo.EntityToDTO;
import com.h2h.pda.pojo.service.ServiceEntityWrapper;

import java.sql.Timestamp;

public class AuthenticationAttemptEntityWrapper implements EntityToDTO<AuthenticationAttemptEntityWrapper, AuthenticationAttemptEntity> {

    private long id;
    private String username;
    private String userAgent;
    private String ipAddress;
    private LoginType loginType;
    private String reason;
    private Boolean isSuccess;
    private Timestamp attemptedAt;

    private ServiceEntityWrapper service;

    public long getId() {
        return id;
    }

    public AuthenticationAttemptEntityWrapper setId(long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AuthenticationAttemptEntityWrapper setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public AuthenticationAttemptEntityWrapper setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public AuthenticationAttemptEntityWrapper setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public AuthenticationAttemptEntityWrapper setLoginType(LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public AuthenticationAttemptEntityWrapper setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public AuthenticationAttemptEntityWrapper setSuccess(Boolean success) {
        isSuccess = success;
        return this;
    }

    public Timestamp getAttemptedAt() {
        return attemptedAt;
    }

    public AuthenticationAttemptEntityWrapper setAttemptedAt(Timestamp attemptedAt) {
        this.attemptedAt = attemptedAt;
        return this;
    }

    public ServiceEntityWrapper getService() {
        return service;
    }

    public AuthenticationAttemptEntityWrapper setService(ServiceEntityWrapper service) {
        this.service = service;
        return this;
    }

    @Override
    public AuthenticationAttemptEntityWrapper wrap(AuthenticationAttemptEntity entity) {
        setId(entity.getId());
        setAttemptedAt(entity.getAttemptedAt());
        setReason(entity.getReason());
        setIpAddress(entity.getIpAddress());
        setSuccess(entity.getSuccess());
        setLoginType(entity.getLoginType());
        setUserAgent(entity.getUserAgent());
        setUsername(entity.getUsername());
        setService(new ServiceEntityWrapper().wrap(entity.getService()));
        return this;
    }

    @Override
    public AuthenticationAttemptEntity unWrap() {
        AuthenticationAttemptEntity entity = new AuthenticationAttemptEntity();
        entity.setId(getId());
        entity.setAttemptedAt(getAttemptedAt());
        entity.setReason(getReason());
        entity.setIpAddress(getIpAddress());
        entity.setSuccess(getSuccess());
        entity.setLoginType(getLoginType());
        entity.setUserAgent(getUserAgent());
        return entity;
    }


}
