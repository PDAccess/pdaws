package com.h2h.pda.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

@Embeddable
public class GroupUserPK implements Serializable {
    @Column(name = "groupid")
    private String groupId;

    @Column(name = "userid")
    private String userId;

    public GroupUserPK() {
    }

    public GroupUserPK(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public GroupUserPK setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public GroupUserPK setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUserPK that = (GroupUserPK) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GroupUserPK.class.getSimpleName() + "[", "]")
                .add("groupId='" + groupId + "'")
                .add("userId='" + userId + "'")
                .toString();
    }
}