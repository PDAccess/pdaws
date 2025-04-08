package com.h2h.pda.pojo.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceInfo {
    @JsonProperty("service_id")
    private String serviceId;
    @JsonProperty("group_id")
    private String groupId;
    @JsonProperty("service_name")
    private String serviceName;
    @JsonProperty("group_name")
    private String groupName;

    public ServiceInfo() {
    }

    public ServiceInfo(String serviceId, String groupId, String serviceName, String groupName) {
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.serviceName = serviceName;
        this.groupName = groupName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
