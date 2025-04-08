package com.h2h.pda.pojo.service;

import java.sql.Timestamp;
import java.util.List;

public class ServiceGroupsParams {
    private Timestamp expireDate;
    private List<String> groupList;

    public ServiceGroupsParams() {
    }

    public ServiceGroupsParams(Timestamp expireDate, List<String> groupList) {
        this.expireDate = expireDate;
        this.groupList = groupList;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }
}
