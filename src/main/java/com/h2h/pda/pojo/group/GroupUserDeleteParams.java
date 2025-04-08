package com.h2h.pda.pojo.group;

public class GroupUserDeleteParams {
    String groupid;
    String userid;

    public GroupUserDeleteParams() {
    }

    public GroupUserDeleteParams(String groupid, String userid) {
        this.groupid = groupid;
        this.userid = userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
