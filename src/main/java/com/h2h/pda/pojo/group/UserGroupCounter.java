package com.h2h.pda.pojo.group;

import java.io.Serializable;

public class UserGroupCounter implements Serializable {
    private long all;
    private long yours;
    private long joined;

    public long getAll() {
        return all;
    }

    public UserGroupCounter setAll(long all) {
        this.all = all;
        return this;
    }

    public long getYours() {
        return yours;
    }

    public UserGroupCounter setYours(long yours) {
        this.yours = yours;
        return this;
    }

    public long getJoined() {
        return joined;
    }

    public UserGroupCounter setJoined(long joined) {
        this.joined = joined;
        return this;
    }
}
