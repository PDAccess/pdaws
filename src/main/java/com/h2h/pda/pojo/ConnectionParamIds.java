package com.h2h.pda.pojo;

public class ConnectionParamIds {

    private String groupId;
    private String serviceId;
    private String credentialId;

    public ConnectionParamIds() {
    }

    public ConnectionParamIds(String groupId, String serviceId, String credentialId) {
        this.groupId = groupId;
        this.serviceId = serviceId;
        this.credentialId = credentialId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
