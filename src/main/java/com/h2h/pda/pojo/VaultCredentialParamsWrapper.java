package com.h2h.pda.pojo;

import com.h2h.pda.pojo.group.GroupParams;
import com.h2h.pda.pojo.permission.Permissions;
import com.h2h.pda.pojo.service.ServiceParams;

import java.sql.Timestamp;
import java.util.Set;

public class VaultCredentialParamsWrapper implements EntityToDTO<VaultCredentialParamsWrapper, VaultCredentialParams> {

    private String credentialId;
    private String username;
    private int connectionUserId;
    private String connectionUserName;
    private String permissionUserId;
    private String permission;
    private ServiceParams service;
    private GroupParams group;
    private Set<Permissions> permissionsSet;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastBreakedAt;
    private Timestamp lastChangedAt;
    private boolean lastChangeStatus;
    private boolean isCheck;
    private boolean checkout;

    public VaultCredentialParamsWrapper() {

    }

    public VaultCredentialParamsWrapper(VaultCredentialParams vcp) {
        wrap(vcp);
    }

    public String getCredentialId() {
        return credentialId;
    }

    public VaultCredentialParamsWrapper setCredentialId(String credentialId) {
        this.credentialId = credentialId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public VaultCredentialParamsWrapper setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getConnectionUserId() {
        return connectionUserId;
    }

    public VaultCredentialParamsWrapper setConnectionUserId(int connectionUserId) {
        this.connectionUserId = connectionUserId;
        return this;
    }

    public String getConnectionUserName() {
        return connectionUserName;
    }

    public VaultCredentialParamsWrapper setConnectionUserName(String connectionUserName) {
        this.connectionUserName = connectionUserName;
        return this;
    }

    public String getPermissionUserId() {
        return permissionUserId;
    }

    public VaultCredentialParamsWrapper setPermissionUserId(String permissionUserId) {
        this.permissionUserId = permissionUserId;
        return this;
    }

    public String getPermission() {
        return permission;
    }

    public VaultCredentialParamsWrapper setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public void setService(ServiceParams service) {
        this.service = service;
    }

    public GroupParams getGroup() {
        return group;
    }

    public void setGroup(GroupParams group) {
        this.group = group;
    }

    public ServiceParams getService() {
        return service;
    }

    public Set<Permissions> getPermissionsSet() {
        return permissionsSet;
    }

    public void setPermissionsSet(Set<Permissions> permissionsSet) {
        this.permissionsSet = permissionsSet;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public VaultCredentialParamsWrapper wrap(VaultCredentialParams entity) {
        setCredentialId(entity.getCredentialEntity().getCredentialId());
        setUsername(entity.getCredentialEntity().getUsername());
        setGroup(new GroupParams().wrap(entity.getCredentialEntity().getGroup()));
        setCreatedAt(entity.getCredentialEntity().getCreatedAt());
        setUpdatedAt(entity.getCredentialEntity().getUpdatedAt());
        setPermissionsSet(entity.getPermissionEntity().getPermissionsSet());
        if (entity.getCredentialEntity().getConnectionUser() != null) {
            setConnectionUserId(entity.getCredentialEntity().getConnectionUser().getId());
            setConnectionUserName(entity.getCredentialEntity().getConnectionUser().getUsername());
            setService(new ServiceParams().wrap(entity.getCredentialEntity().getConnectionUser().getServiceEntity()));
        }

        return this;
    }

    @Override
    public VaultCredentialParams unWrap() {
        return null;
    }

    public Timestamp getLastBreakedAt() {
        return lastBreakedAt;
    }

    public void setLastBreakedAt(Timestamp lastBreakedAt) {
        this.lastBreakedAt = lastBreakedAt;
    }

    public Timestamp getLastChangedAt() {
        return lastChangedAt;
    }

    public void setLastChangedAt(Timestamp lastChangedAt) {
        this.lastChangedAt = lastChangedAt;
    }

    public boolean isLastChangeStatus() {
        return lastChangeStatus;
    }

    public void setLastChangeStatus(boolean lastChangeStatus) {
        this.lastChangeStatus = lastChangeStatus;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckout() {
        return checkout;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }
}
