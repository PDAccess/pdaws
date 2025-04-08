package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecTrace;

// @TODO: Entity Fix
public class ExecTraceWrapper {
    private ExecTrace execTrace;
    private String serviceName;

    public ExecTraceWrapper(ExecTrace execTrace, String serviceName) {
        this.execTrace = execTrace;
        this.serviceName = serviceName;
    }

    public ExecTrace getExecTrace() {
        return execTrace;
    }

    public void setExecTrace(ExecTrace execTrace) {
        this.execTrace = execTrace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}