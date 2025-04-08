package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecFile;

// @TODO: Entity Fix
public class ExecFileWrapper {
    private ExecFile execFile;
    private String serviceName;

    public ExecFileWrapper(ExecFile execFile, String serviceName) {
        this.execFile = execFile;
        this.serviceName = serviceName;
    }

    public ExecFile getExecFile() {
        return execFile;
    }

    public void setExecFile(ExecFile execFile) {
        this.execFile = execFile;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}