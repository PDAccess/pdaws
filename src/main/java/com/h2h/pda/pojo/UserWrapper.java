package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.user.UserRole;

import java.sql.Timestamp;

@Deprecated
public class UserWrapper implements EntityToDTO<UserWrapper, UserEntity> {
    private String userId;

    private String email;
    private String phone;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("remember_token")
    private String rememberToken;
    private String username;
    private UserRole role;
    private Boolean external;
    private Timestamp blocked;
    private String status;
    @JsonProperty("twofactorauth")
    private Boolean twoFactorAuth;
    private Boolean notification;
    @JsonProperty("ldap_dn")
    private String ldapDn;

    @JsonProperty("create_at")
    Timestamp createdAt;
    @JsonProperty("updated_at")
    Timestamp updatedAt;
    @JsonProperty("deleted_at")
    Timestamp deletedAt;

    public UserWrapper() {
    }

    public UserWrapper(UserEntity userEntity) {
        wrap(userEntity);
    }

    private MfaVerification mfaVerification;

    public MfaVerification getMfaVerification() {
        return mfaVerification;
    }

    public void setMfaVerification(MfaVerification mfaVerification) {
        this.mfaVerification = mfaVerification;
    }

    public String getUserId() {
        return userId;
    }

    public UserWrapper setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserWrapper setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserWrapper setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserWrapper setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserWrapper setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public UserWrapper setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserWrapper setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserWrapper setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public Boolean getExternal() {
        return external;
    }

    public UserWrapper setExternal(Boolean external) {
        this.external = external;
        return this;
    }

    public Timestamp getBlocked() {
        return blocked;
    }

    public UserWrapper setBlocked(Timestamp blocked) {
        this.blocked = blocked;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserWrapper setStatus(String status) {
        this.status = status;
        return this;
    }

    public Boolean getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public UserWrapper setTwoFactorAuth(Boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
        return this;
    }

    public Boolean getNotification() {
        return notification;
    }

    public UserWrapper setNotification(Boolean notification) {
        this.notification = notification;
        return this;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public UserWrapper setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
        return this;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public UserWrapper setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public UserWrapper setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public UserWrapper setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    @Override
    public UserWrapper wrap(UserEntity userEntity) {
        this.setUsername(userEntity.getUsername());
        this.setRole(userEntity.getRole());
        this.setEmail(userEntity.getEmail());
        this.setFirstName(userEntity.getFirstName());
        this.setLastName(userEntity.getLastName());
        this.setTwoFactorAuth(userEntity.getTwofactorauth());
        this.setPhone(userEntity.getPhone());
        this.setExternal(userEntity.isExternal());
        this.setUserId(userEntity.getUserId());
        this.setBlocked(userEntity.getBlocked());
        this.setNotification(userEntity.getNotification());
        this.setRememberToken(userEntity.getRememberToken());
        this.setStatus(userEntity.getStatus());
        this.setLdapDn(userEntity.getLdapDn());
        this.setCreatedAt(userEntity.getCreatedAt());
        this.setUpdatedAt(userEntity.getUpdatedAt());
        this.setDeletedAt(userEntity.getDeletedAt());
        return this;
    }

    @Override
    public UserEntity unWrap() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(this.getUsername());
        userEntity.setRole(this.getRole());
        userEntity.setEmail(this.getEmail());
        userEntity.setFirstName(this.getFirstName());
        userEntity.setLastName(this.getLastName());
        userEntity.setPhone(this.getPhone());
        userEntity.setExternal(this.getExternal());
        userEntity.setUserId(this.getUserId());
        userEntity.setBlocked(this.getBlocked());
        userEntity.setNotification(this.getNotification());
        userEntity.setRememberToken(this.getRememberToken());
        userEntity.setStatus(this.getStatus());
        userEntity.setLdapDn(this.getLdapDn());
        userEntity.setCreatedAt(this.getCreatedAt());
        userEntity.setUpdatedAt(this.getUpdatedAt());
        userEntity.setDeletedAt(this.getDeletedAt());
        return userEntity;
    }
}
