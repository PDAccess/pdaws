package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.pojo.user.UserRole;
import com.h2h.pda.pojo.user.UserShell;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity extends DeletableBaseEntity implements Serializable {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String email;
    private String phone;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;
    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;
    @Column(name = "remember_token")
    @JsonProperty("remember_token")
    private String rememberToken;

    @Column(name = "username")
    private String username;

    @Column(name = "urole")
    private UserRole role;

    private Boolean external;
    private Timestamp blocked;
    private String status;
    @Column(name = "mfa")
    private Boolean twofactorauth;
    private Boolean notification;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "userEntities")
    @Deprecated
    private Set<AlarmEntity> alarmEntities;

    @Column(name = "ldap_dn")
    private String ldapDn;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<BreakTheGlassShareEntity> shares;

    @Column(name = "shell")
    private UserShell shell;

    public UserEntity() {
        // Constructor
    }

    public String getUserId() {
        return userId;
    }

    public UserEntity setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole uRole) {
        this.role = uRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isExternal() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getTwofactorauth() {
        return twofactorauth;
    }

    public void setTwofactorauth(Boolean twofactorauth) {
        this.twofactorauth = twofactorauth;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public Set<AlarmEntity> getAlarmEntities() {
        return alarmEntities;
    }

    public void setAlarmEntities(Set<AlarmEntity> alarmEntities) {
        this.alarmEntities = alarmEntities;
    }

    public String getLdapDn() {
        return ldapDn;
    }

    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    public List<BreakTheGlassShareEntity> getShares() {
        return shares;
    }

    public void setShares(List<BreakTheGlassShareEntity> shares) {
        this.shares = shares;
    }

    public UserShell getShell() {
        return shell;
    }

    public void setShell(UserShell shell) {
        this.shell = shell;
    }
}

