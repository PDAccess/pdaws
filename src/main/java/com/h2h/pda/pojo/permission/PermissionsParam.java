package com.h2h.pda.pojo.permission;

import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.CredentialDetails;

import java.util.List;

public class PermissionsParam {

    private List<UserEntity> users;
    private List<PermissionBaseParam> permissions;
    private List<CredentialDetails> credentials;

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<PermissionBaseParam> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionBaseParam> permissions) {
        this.permissions = permissions;
    }


    public List<CredentialDetails> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<CredentialDetails> credentials) {
        this.credentials = credentials;
    }
}
