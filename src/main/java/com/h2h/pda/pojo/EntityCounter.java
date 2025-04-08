package com.h2h.pda.pojo;

import java.io.Serializable;

public class EntityCounter implements Serializable {
    private long members;
    private long credential;
    private long policy;
    private long alarm;

    public long getMembers() {
        return members;
    }

    public EntityCounter setMembers(long members) {
        this.members = members;
        return this;
    }

    public long getCredential() {
        return credential;
    }

    public EntityCounter setCredential(long credential) {
        this.credential = credential;
        return this;
    }

    public long getPolicy() {
        return policy;
    }

    public EntityCounter setPolicy(long policy) {
        this.policy = policy;
        return this;
    }

    public long getAlarm() {
        return alarm;
    }

    public EntityCounter setAlarm(long alarm) {
        this.alarm = alarm;
        return this;
    }
}
