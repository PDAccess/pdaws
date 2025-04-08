package com.h2h.pda.pojo;

public class UserServiceDeleteParams {
    String userid;
    String serviceid;

    public UserServiceDeleteParams() {
    }

    public UserServiceDeleteParams(String userid, String serviceid) {
        this.userid = userid;
        this.serviceid = serviceid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }
}
