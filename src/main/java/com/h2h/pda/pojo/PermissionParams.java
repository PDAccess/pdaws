package com.h2h.pda.pojo;

import com.h2h.pda.entity.PermissionEntity;
import com.h2h.pda.pojo.permission.Permissions;

import java.util.Set;

public class PermissionParams implements EntityToDTO<PermissionParams, PermissionEntity> {

    private int id;
    private String credentialId;
    private UserParams user;
    private Set<Permissions> permissionsSet;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public UserParams getUser() {
        return user;
    }

    public void setUser(UserParams user) {
        this.user = user;
    }

    public Set<Permissions> getPermissionsSet() {
        return permissionsSet;
    }

    public void setPermissionsSet(Set<Permissions> permissionsSet) {
        this.permissionsSet = permissionsSet;
    }

    @Override
    public PermissionParams wrap(PermissionEntity entity) {
        if (entity != null) {
            setId(entity.getPermissionId());
            setCredentialId(entity.getCredential().getCredentialId());
            setUser(new UserParams().wrap(entity.getUser()));
            setPermissionsSet(entity.getPermissionsSet());
            return this;
        }
        return null;
    }

    @Override
    public PermissionEntity unWrap() {
        return null;
    }
}
