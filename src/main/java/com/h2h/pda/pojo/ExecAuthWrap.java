package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecAuthWrap {
    private String function;
    private String flags;
    private String service;
    private String user;
    @JsonProperty("terminal")
    private String tty;
    @JsonProperty("date")
    private String time;
    @JsonProperty("ruser")
    private String rUser;
    @JsonProperty("rhost")
    private String rHost;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTty() {
        return tty;
    }

    public void setTty(String tty) {
        this.tty = tty;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getrUser() {
        return rUser;
    }

    public void setrUser(String rUser) {
        this.rUser = rUser;
    }

    public String getrHost() {
        return rHost;
    }

    public void setrHost(String rHost) {
        this.rHost = rHost;
    }
}
