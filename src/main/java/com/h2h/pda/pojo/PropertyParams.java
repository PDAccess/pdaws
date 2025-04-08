package com.h2h.pda.pojo;

public class PropertyParams {
    private String groupId;
    private String serviceId;
    private String key;
    private String value;

    public PropertyParams(String groupId, String serviceId, String key, String value) {
        this.groupId = groupId;
        this.serviceId = serviceId;
        this.key = key;
        this.value = value;
    }

    public PropertyParams() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
