package com.h2h.pda.pojo.service;

import com.h2h.pda.pojo.UserPolicyWrapper;
import com.h2h.pda.pojo.group.GroupUserWrapper;

import java.util.List;

public class ServicePermissionsWrapper {

    List<UserPolicyWrapper> userPolicyWrappers;
    List<GroupUserWrapper> groupuserslist;

    public ServicePermissionsWrapper() {
    }

    public ServicePermissionsWrapper(List<UserPolicyWrapper> userPolicyWrappers, List<GroupUserWrapper> groupuserslist) {
        this.userPolicyWrappers = userPolicyWrappers;
        this.groupuserslist = groupuserslist;
    }

    public List<UserPolicyWrapper> getUserPolicyWrappers() {
        return userPolicyWrappers;
    }

    public void setUserPolicyWrappers(List<UserPolicyWrapper> userPolicyWrappers) {
        this.userPolicyWrappers = userPolicyWrappers;
    }

    public List<GroupUserWrapper> getGroupuserslist() {
        return groupuserslist;
    }

    public void setGroupuserslist(List<GroupUserWrapper> groupuserslist) {
        this.groupuserslist = groupuserslist;
    }
}
