package com.h2h.pda.pojo.permission;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.entity.UserEntity;

public class PermissionParam {

    private String key;
    private boolean value;
    private PermissionEntity permission;
    private UserEntity user;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
