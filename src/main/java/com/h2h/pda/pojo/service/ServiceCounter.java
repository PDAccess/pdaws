package com.h2h.pda.pojo.service;

import com.h2h.pda.pojo.EntityCounter;

import java.io.Serializable;

public class ServiceCounter extends EntityCounter implements Serializable {
    private long groups;

    public long getGroups() {
        return groups;
    }

    public ServiceCounter setGroups(long groups) {
        this.groups = groups;
        return this;
    }
}
