package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.UserEntity;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.user.UserShell;

import java.sql.Timestamp;
import java.util.List;

public class UserDTO implements EntityToDTO<UserDTO, UserEntity> {

    private String userId;
    @JsonProperty("created_at")
    private Timestamp createdAt;
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String phone;
    @JsonProperty("remember_token")
    private String rememberToken;

    @JsonProperty("updated_at")
    private Timestamp updatedAt;
    private UserRole role;
    private String username;
    @JsonProperty("deleted_at")
    private Timestamp deletedAt;
    private Boolean external;
    private Timestamp blocked;
    private List<String> ipAddress;
    private Boolean notification;

    private String status;
    @JsonProperty("twofactorauth")
    private Boolean twoFactorAuth;
    @JsonProperty("ldap_dn")
    private String ldapDn;

    private UserShell shell;

    public UserDTO() {

    }

    public UserDTO(UserEntity userEntity) {
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Timestamp getBlocked() {
        return blocked;
    }

    public void setBlocked(Timestamp blocked) {
        this.blocked = blocked;
    }

    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getNotification() {
        return notification;
    }

    public UserDTO setNotification(Boolean notification) {
        this.notification = notification;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public Boolean getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public UserDTO setTwoFactorAuth(Boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
        return this;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public UserDTO setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
        return this;
    }

    public UserShell getShell() {
        return shell;
    }

    public void setShell(UserShell shell) {
        this.shell = shell;
    }

    @Override
    public UserDTO wrap(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
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
        this.setShell(userEntity.getShell());
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
        userEntity.setShell(this.getShell());
        userEntity.setLdapDn(this.getLdapDn());
        userEntity.setCreatedAt(this.getCreatedAt());
        userEntity.setUpdatedAt(this.getUpdatedAt());
        userEntity.setDeletedAt(this.getDeletedAt());
        return userEntity;
    }
}
