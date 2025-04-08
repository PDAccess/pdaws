package com.h2h.pda.pojo.permission;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.entity.UserEntity;

public class PermissionWrapper {

    private UserEntity userEntity;
    private PermissionEntity permissionEntity;

    public PermissionWrapper(UserEntity userEntity, PermissionEntity permissionEntity) {
        this.userEntity = userEntity;
        this.permissionEntity = permissionEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }
}
