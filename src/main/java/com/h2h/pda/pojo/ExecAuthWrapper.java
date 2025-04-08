package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecAuth;

// @TODO: Entity Fix
public class ExecAuthWrapper {
    private ExecAuth execAuth;
    private String serviceName;

    public ExecAuthWrapper(ExecAuth execAuth, String serviceName) {
        this.execAuth = execAuth;
        this.serviceName = serviceName;
    }

    public ExecAuth getExecAuth() {
        return execAuth;
    }

    public void setExecAuth(ExecAuth execAuth) {
        this.execAuth = execAuth;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}