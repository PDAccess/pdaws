package com.h2h.pda.pojo.group;

import com.h2h.pda.pojo.EntityCounter;

import java.io.Serializable;

public class GroupCounter extends EntityCounter implements Serializable {
    private long service;

    public long getService() {
        return service;
    }

    public GroupCounter setService(long service) {
        this.service = service;
        return this;
    }
}
