package com.h2h.pda.jwt;

import com.h2h.pda.pojo.auth.LoginType;
import com.h2h.pda.pojo.user.UserRole;

import java.io.Serializable;

public final class TokenDetails implements Serializable {
    private String token;

    public String getAuthId() {
        return authId;
    }

    public TokenDetails setAuthId(String authId) {
        this.authId = authId;
        return this;
    }

    private String authId;
    private UserRole role;
    private String remoteAddress;
    private String userAgent;
    private LoginType loginType;
    private String serviceId;
    private LoginTypes authType;


    public TokenDetails() {
    }

    public String getToken() {
        return token;
    }

    public TokenDetails setToken(String token) {
        this.token = token;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public TokenDetails setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public TokenDetails setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public TokenDetails setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public TokenDetails setLoginType(LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public LoginTypes getAuthType() {
        return authType;
    }

    public void setAuthType(LoginTypes authType) {
        this.authType = authType;
    }
}
