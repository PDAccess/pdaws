package com.h2h.pda.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "groupservice")
public class GroupServiceEntity {

    @EmbeddedId
    private GroupServicePK id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "groupid", insertable = false, updatable = false)
    GroupsEntity group;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "serviceid", insertable = false, updatable = false)
    ServiceEntity service;

    public GroupServiceEntity() {
    }

    public GroupServiceEntity(GroupServicePK id) {
        this.id = id;
    }

    public GroupServicePK getId() {
        return id;
    }

    public void setId(GroupServicePK id) {
        this.id = id;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public GroupServiceEntity setGroup(GroupsEntity group) {
        this.group = group;
        return this;
    }

    public ServiceEntity getService() {
        return service;
    }

    public GroupServiceEntity setService(ServiceEntity service) {
        this.service = service;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupServiceEntity that = (GroupServiceEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}