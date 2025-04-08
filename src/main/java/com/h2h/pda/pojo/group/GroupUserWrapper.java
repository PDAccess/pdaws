package com.h2h.pda.pojo.group;

import com.h2h.pda.entity.GroupsEntity;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.UserDTO;

import java.sql.Timestamp;

public class GroupUserWrapper {
    UserDTO user;
    GroupsEntityWrapper group;
    String membershipType;
    String membershipRole;
    Timestamp createdAt;

    public GroupUserWrapper() {
    }

    public GroupUserWrapper(UserEntity user, GroupsEntity group) {
        this.user = new UserDTO(user);
        this.group = new GroupsEntityWrapper(group);
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public GroupsEntityWrapper getGroup() {
        return group;
    }

    public void setGroup(GroupsEntityWrapper group) {
        this.group = group;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getMembershipRole() {
        return membershipRole;
    }

    public void setMembershipRole(String membershipRole) {
        this.membershipRole = membershipRole;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
