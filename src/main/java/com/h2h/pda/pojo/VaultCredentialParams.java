package com.h2h.pda.pojo;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.entity.PermissionEntity;

public class VaultCredentialParams {

    private CredentialEntity credentialEntity;
    private PermissionEntity permissionEntity;

    public VaultCredentialParams(CredentialEntity credentialEntity, PermissionEntity permissionEntity) {
        this.credentialEntity = credentialEntity;
        this.permissionEntity = permissionEntity;
    }

    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
    }

    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }
}
