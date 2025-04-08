package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.ExecShellSessionEntity;
import com.h2h.pda.entity.ServiceEntity;

public class ExecShellSessionInfo {

    @JsonProperty("session")
    private ExecShellSessionEntity execShellSessionEntity;

    @JsonProperty("service")
    private ServiceEntity serviceEntity;

    public ExecShellSessionEntity getExecShellSessionEntity() {
        return execShellSessionEntity;
    }

    public void setExecShellSessionEntity(ExecShellSessionEntity execShellSessionEntity) {
        this.execShellSessionEntity = execShellSessionEntity;
    }

    public ServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(ServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

}
