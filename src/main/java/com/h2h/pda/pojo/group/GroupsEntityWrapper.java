package com.h2h.pda.pojo.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.pojo.EntityToDTO;

import java.sql.Timestamp;

public class GroupsEntityWrapper implements EntityToDTO<GroupsEntityWrapper, GroupsEntity> {

    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("groupname")
    private String groupName;
    private String description;
    private String parent;
    @JsonProperty("grouptype")
    private String groupType;
    private String ldapRdn;
    private String ldapDn;
    private String groupCategory;
    private Timestamp createTime;
    private GroupCounter groupCounter;
    private GroupRole ownMembership;

    public GroupsEntityWrapper() {

    }

    public GroupsEntityWrapper(GroupsEntity group) {
        wrap(group);
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupsEntityWrapper setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GroupsEntityWrapper setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getParent() {
        return parent;
    }

    public GroupsEntityWrapper setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public String getGroupType() {
        return groupType;
    }

    public GroupsEntityWrapper setGroupType(String groupType) {
        this.groupType = groupType;
        return this;
    }

    public String getLdapRdn() {
        return ldapRdn;
    }

    public GroupsEntityWrapper setLdapRdn(String ldapRdn) {
        this.ldapRdn = ldapRdn;
        return this;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public GroupsEntityWrapper setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
        return this;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public GroupsEntityWrapper setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
        return this;
    }

    public GroupCounter getGroupCounter() {
        return groupCounter;
    }

    public GroupsEntityWrapper setGroupCounter(GroupCounter groupCounter) {
        this.groupCounter = groupCounter;
        return this;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public GroupsEntityWrapper setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public GroupsEntityWrapper setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    @Override
    public GroupsEntityWrapper wrap(GroupsEntity entity) {
        setGroupId(entity.getGroupId());
        setGroupName(entity.getGroupName());
        setCreateTime(entity.getCreatedAt());
        setDescription(entity.getDescription());
        return this;
    }

    @Override
    public GroupsEntity unWrap() {
        GroupsEntity entity = new GroupsEntity();
        entity.setGroupId(getGroupId());
        return entity;
    }

    public GroupRole getOwnMembership() {
        return ownMembership;
    }

    public void setOwnMembership(GroupRole ownMembership) {
        this.ownMembership = ownMembership;
    }
}
