package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class UserPolicyWrapper {
    UserDTO user;
    Timestamp expiredatetime;
    String policyname;
    Timestamp createdAt;


    public UserPolicyWrapper() {
    }

    public UserPolicyWrapper(UserDTO user, Timestamp expiredatetime, String policyname, Timestamp createdAt) {
        this.user = user;
        this.expiredatetime = expiredatetime;
        this.policyname = policyname;
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

    public String getPolicyname() {
        return policyname;
    }

    public void setPolicyname(String policyname) {
        this.policyname = policyname;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
