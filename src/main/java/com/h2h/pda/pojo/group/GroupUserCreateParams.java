package com.h2h.pda.pojo.group;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

public class GroupUserCreateParams {
    List<String> userlist;
    Timestamp expiredate;
    String expiretime;
    String role;

    @JsonProperty("connection_user")
    int connectionUser;

    public GroupUserCreateParams() {
    }

    public GroupUserCreateParams(List<String> userlist, Timestamp expiredate, String expiretime) {
        this.userlist = userlist;
        this.expiredate = expiredate;
        this.expiretime = expiretime;
    }

    public List<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<String> userlist) {
        this.userlist = userlist;
    }

    public Timestamp getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(Timestamp expiredate) {
        this.expiredate = expiredate;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getConnectionUser() {
        return connectionUser;
    }

    public void setConnectionUser(int connectionUser) {
        this.connectionUser = connectionUser;
    }

    public GroupRole getMembershipRole() {
        GroupRole groupRole;
        if (GroupRole.ADMIN.name().equals(role)) {
            groupRole = GroupRole.ADMIN;
        } else {
            groupRole = GroupRole.USER;
        }
        return groupRole;
    }

    @Override
    public String toString() {
        return "GroupUserCreateParams{" +
                "userlist=" + userlist +
                ", expiredate=" + expiredate +
                ", expiretime='" + expiretime + '\'' +
                ", connectionUser=" + connectionUser +
                '}';
    }
}
