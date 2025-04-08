package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "credentials")
public class CredentialEntity extends WhoCreateEntity {

    @Id
    @Column(name = "id")
    private String credentialId;

    @Column(name = "username")
    private String username;

    @Column(name = "check_status")
    private boolean checkStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_user", referencedColumnName = "id")
    private ConnectionUserEntity connectionUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "groupId")
    private GroupsEntity group;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credentialEntity")
    private List<BreakTheGlassEntity> breakTheGlassEntities;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credentialEntity")
    private List<BreakTheGlassShareEntity> breakTheGlassShareEntities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credential")
    private List<PermissionEntity> permissionEntities;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "credantial_id")
    private AutoCredantialSettingsEntity autoCredantialSettingsEntity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private CredentialEntity parentCredential;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCredential")
    private Set<CredentialEntity> childCredentials;

    @Column(name = "credential_manage_time")
    private Integer credentialManageTime;

    @Column(name = "credential_manage_time_type")
    private String credentialManageTimeType;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "credentialEntity")
    private Set<SessionEntity> sessionEntities;

    @PreRemove
    private void preRemove() {
        for (SessionEntity s : sessionEntities) {
            s.setCredentialEntity(null);
        }
    }

    public String getCredentialId() {
        return credentialId;
    }

    public CredentialEntity setCredentialId(String credentialId) {
        this.credentialId = credentialId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public CredentialEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    public ConnectionUserEntity getConnectionUser() {
        return connectionUser;
    }

    public CredentialEntity setConnectionUser(ConnectionUserEntity connectionUser) {
        this.connectionUser = connectionUser;
        return this;
    }

    public GroupsEntity getGroup() {
        return group;
    }

    public CredentialEntity setGroup(GroupsEntity group) {
        this.group = group;
        return this;
    }

    public List<BreakTheGlassEntity> getBreakTheGlassEntities() {
        return breakTheGlassEntities;
    }

    public CredentialEntity setBreakTheGlassEntities(List<BreakTheGlassEntity> breakTheGlassEntities) {
        this.breakTheGlassEntities = breakTheGlassEntities;
        return this;
    }

    public List<PermissionEntity> getPermissionEntities() {
        return permissionEntities;
    }

    public void setPermissionEntities(List<PermissionEntity> permissionEntities) {
        this.permissionEntities = permissionEntities;
    }

    public List<BreakTheGlassShareEntity> getBreakTheGlassShareEntities() {
        return breakTheGlassShareEntities;
    }

    public void setBreakTheGlassShareEntities(List<BreakTheGlassShareEntity> breakTheGlassShareEntities) {
        this.breakTheGlassShareEntities = breakTheGlassShareEntities;
    }

    public AutoCredantialSettingsEntity getAutoCredantialSettingsEntity() {
        return autoCredantialSettingsEntity;
    }

    public void setAutoCredantialSettingsEntity(AutoCredantialSettingsEntity autoCredantialSettingsEntity) {
        this.autoCredantialSettingsEntity = autoCredantialSettingsEntity;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public CredentialEntity getParentCredential() {
        return parentCredential;
    }

    public void setParentCredential(CredentialEntity parentCredential) {
        this.parentCredential = parentCredential;
    }

    public Set<CredentialEntity> getChildCredentials() {
        return childCredentials;
    }

    public void setChildCredentials(Set<CredentialEntity> childCredentials) {
        this.childCredentials = childCredentials;
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

    public Set<SessionEntity> getSessionEntities() {
        return sessionEntities;
    }

    public void setSessionEntities(Set<SessionEntity> sessionEntities) {
        this.sessionEntities = sessionEntities;
    }
}
