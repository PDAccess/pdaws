package com.h2h.pda.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.h2h.pda.pojo.group.GroupCategory;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "groups")
public class GroupsEntity extends DeletableBaseEntity {

    @Id
    @Column(name = "groupid")
    private String groupId;

    @Column(name = "groupname")
    private String groupName;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent")
    private GroupsEntity parent;

    @Column(name = "grouptype")
    @Deprecated
    private String groupType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "groupsEntity")
    @Deprecated
    private Set<AlarmEntity> alarmEntities;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_properties", joinColumns = @JoinColumn(name = "group_id"))
    private Set<GroupProperty> properties;

    @Column(name = "group_category")
    @Enumerated(EnumType.STRING)
    private GroupCategory groupCategory;

    @Column(name = "ldap_rdn")
    private String ldapRdn;

    @Column(name = "ldap_dn")
    private String ldapDn;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
    private Set<GroupUserEntity> members;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "group")
    private Set<GroupServiceEntity> services;

    public GroupsEntity() {
        // Constructor
    }

    public String getGroupId() {
        return groupId;
    }

    public GroupsEntity setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupsEntity setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupsEntity getParent() {
        return parent;
    }

    public void setParent(GroupsEntity parent) {
        this.parent = parent;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Deprecated
    public Set<AlarmEntity> getAlarmEntities() {
        return alarmEntities;
    }

    @Deprecated
    public void setAlarmEntities(Set<AlarmEntity> alarmEntities) {
        this.alarmEntities = alarmEntities;
    }

    public GroupCategory getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(GroupCategory groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getLdapRdn() {
        return ldapRdn;
    }

    public void setLdapRdn(String ldapRdn) {
        this.ldapRdn = ldapRdn;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    public Set<GroupProperty> getProperties() {
        return properties;
    }

    public GroupsEntity setProperties(Set<GroupProperty> properties) {
        this.properties = properties;
        return this;
    }

    public Set<GroupUserEntity> getMembers() {
        return members;
    }

    public GroupsEntity setMembers(Set<GroupUserEntity> members) {
        this.members = members;
        return this;
    }

    public Set<GroupServiceEntity> getServices() {
        return services;
    }

    public GroupsEntity setServices(Set<GroupServiceEntity> services) {
        this.services = services;
        return this;
    }
}
