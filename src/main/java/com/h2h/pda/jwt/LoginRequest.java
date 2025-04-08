package com.h2h.pda.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("loginType")
    private String loginType;
    @JsonProperty("service")
    private String service;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public LoginRequest setService(String service) {
        this.service = service;
        return this;
    }
}
