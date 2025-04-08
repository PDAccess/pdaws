package com.h2h.pda.pojo.group;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.UserDTO;

import java.sql.Timestamp;

public class GroupUserGetParams {
    UserDTO user;
    Timestamp expiredatetime;
    Timestamp createdAt;
    String membershipType;
    String membershipRole;

    public GroupUserGetParams() {
    }

    public GroupUserGetParams(UserEntity user, Timestamp expiredatetime, Timestamp createdAt) {
        this.user = new UserDTO(user);
        this.expiredatetime = expiredatetime;
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Timestamp getExpiredatetime() {
        return expiredatetime;
    }

    public void setExpiredatetime(Timestamp expiredatetime) {
        this.expiredatetime = expiredatetime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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
}
