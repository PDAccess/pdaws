package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupServicePK implements Serializable {
    @JsonProperty("groupid")
    @Column(name = "groupid")
    private String groupId;

    @JsonProperty("serviceid")
    @Column(name = "serviceid")
    private String serviceId;

    public GroupServicePK() {
    }

    public GroupServicePK(String groupId, String serviceId) {
        this.groupId = groupId;
        this.serviceId = serviceId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupServicePK that = (GroupServicePK) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, serviceId);
    }
}
