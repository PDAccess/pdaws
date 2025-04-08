package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

public class UserServiceCreateParams {
    List<String> users;
    String policyid;
    Timestamp expiredate;
    String expiretime;

    @JsonProperty("connection_user")
    int connectionUser;

    public UserServiceCreateParams() {
    }

    public UserServiceCreateParams(List<String> users, String policyid, Timestamp expiredate, String expiretime) {
        this.users = users;
        this.policyid = policyid;
        this.expiredate = expiredate;
        this.expiretime = expiretime;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getPolicyid() {
        return policyid;
    }

    public void setPolicyid(String policyid) {
        this.policyid = policyid;
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

    public int getConnectionUser() {
        return connectionUser;
    }

    public void setConnectionUser(int connectionUser) {
        this.connectionUser = connectionUser;
    }
}
