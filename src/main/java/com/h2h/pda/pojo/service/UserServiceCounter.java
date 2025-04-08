package com.h2h.pda.pojo.service;

import java.io.Serializable;

public class UserServiceCounter implements Serializable {

    long all;
    long agent;
    long yours;
    long joined;

    public long getAll() {
        return all;
    }

    public UserServiceCounter setAll(long all) {
        this.all = all;
        return this;
    }

    public long getAgent() {
        return agent;
    }

    public UserServiceCounter setAgent(long agent) {
        this.agent = agent;
        return this;
    }

    public long getYours() {
        return yours;
    }

    public UserServiceCounter setYours(long yours) {
        this.yours = yours;
        return this;
    }

    public long getJoined() {
        return joined;
    }

    public UserServiceCounter setJoined(long joined) {
        this.joined = joined;
        return this;
    }
}
