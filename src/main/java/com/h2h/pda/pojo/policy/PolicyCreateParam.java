package com.h2h.pda.pojo.policy;

import java.util.List;

public class PolicyCreateParam {
    private PolicyEntityWrapper policyEntity;
    private List<String> regexList;
    private List<String> userList;
    private List<String> groupList;

    public PolicyCreateParam() {
    }

    public PolicyCreateParam(PolicyEntityWrapper policyEntity, List<String> regexlist) {
        this.policyEntity = policyEntity;
        this.regexList = regexlist;
    }

    public PolicyEntityWrapper getPolicyEntity() {
        return policyEntity;
    }

    public PolicyCreateParam setPolicyEntity(PolicyEntityWrapper policyEntity) {
        this.policyEntity = policyEntity;
        return this;
    }

    public List<String> getRegexList() {
        return regexList;
    }

    public void setRegexList(List<String> regexList) {
        this.regexList = regexList;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }
}
