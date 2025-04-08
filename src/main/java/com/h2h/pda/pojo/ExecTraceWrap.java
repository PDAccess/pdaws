package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class ExecTraceWrap {
    private String name;
    private String param;
    private String user;

    @JsonProperty("user_id")
    private String userId;

    private String time;

    @JsonProperty("login_user")
    private String loginUser;

    @JsonProperty("login_address")
    private String loginAddress;

    @JsonProperty("login_time")
    private Timestamp loginTime;

    public String getName() {
        return name;
    }

    public ExecTraceWrap setName(String name) {
        this.name = name;
        return this;
    }

    public String getParam() {
        return param;
    }

    public ExecTraceWrap setParam(String param) {
        this.param = param;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ExecTraceWrap setUser(String user) {
        this.user = user;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ExecTraceWrap setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ExecTraceWrap setTime(String time) {
        this.time = time;
        return this;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }
}
