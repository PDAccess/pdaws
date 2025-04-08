package com.h2h.pda.pojo;

import com.h2h.pda.entity.CredentialEntity;
import com.h2h.pda.pojo.group.GroupParams;

import java.sql.Timestamp;

public class CredentialDetails implements EntityToDTO<CredentialDetails, CredentialEntity> {

    private String id;
    private String username;
    private GroupParams group;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private UserParams whoCreate;
    private ConnectionUserWrapper account;
    private boolean checkStatus;
    private Timestamp lastAccessTime;
    private Timestamp lastPasswordChangeTime;
    private boolean passwordChangeStatus;
    private Integer credentialManageTime;
    private String credentialManageTimeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GroupParams getGroup() {
        return group;
    }

    public void setGroup(GroupParams group) {
        this.group = group;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public UserParams getWhoCreate() {
        return whoCreate;
    }

    public void setWhoCreate(UserParams whoCreate) {
        this.whoCreate = whoCreate;
    }

    public ConnectionUserWrapper getAccount() {
        return account;
    }

    public void setAccount(ConnectionUserWrapper account) {
        this.account = account;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    @Override
    public CredentialDetails wrap(CredentialEntity entity) {
        setId(entity.getCredentialId());
        setUsername(entity.getUsername());
        setGroup(new GroupParams().wrap(entity.getGroup()));
        setCreatedAt(entity.getCreatedAt());
        setWhoCreate(new UserParams().wrap(entity.getWhoCreate()));
        setAccount(new ConnectionUserWrapper().wrap(entity.getConnectionUser()));
        setCheckStatus(entity.isCheckStatus());
        setCredentialManageTime(entity.getCredentialManageTime());
        setCredentialManageTimeType(entity.getCredentialManageTimeType());
        return this;
    }

    @Override
    public CredentialEntity unWrap() {
        return null;
    }

    public Timestamp getLastPasswordChangeTime() {
        return lastPasswordChangeTime;
    }

    public void setLastPasswordChangeTime(Timestamp lastPasswordChangeTime) {
        this.lastPasswordChangeTime = lastPasswordChangeTime;
    }

    public boolean isPasswordChangeStatus() {
        return passwordChangeStatus;
    }

    public void setPasswordChangeStatus(boolean passwordChangeStatus) {
        this.passwordChangeStatus = passwordChangeStatus;
    }

    public Timestamp getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Timestamp lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Integer getCredentialManageTime() {
        return credentialManageTime;
    }

    public void setCredentialManageTime(Integer credentialManageTime) {
        this.credentialManageTime = credentialManageTime;
    }

    public String getCredentialManageTimeType() {
        return credentialManageTimeType;
    }

    public void setCredentialManageTimeType(String credentialManageTimeType) {
        this.credentialManageTimeType = credentialManageTimeType;
    }
}
