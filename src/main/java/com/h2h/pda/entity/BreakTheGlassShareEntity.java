package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "break_the_glass_shares")
public class BreakTheGlassShareEntity {

    @Id
    private String id;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    private CredentialEntity credentialEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "who_create", referencedColumnName = "user_id")
    private UserEntity userEntity;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "expired_at")
    private Timestamp expiredAt;

    @Column(name = "allow_ip")
    private String allowIpAddress;

    @ManyToMany
    @JoinTable(
            name = "share_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "share_id"))
    private List<UserEntity> users;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getAllowIpAddress() {
        return allowIpAddress;
    }

    public void setAllowIpAddress(String allowIpAddress) {
        this.allowIpAddress = allowIpAddress;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public boolean canShareUser(String userId) {
        if (!hasUser()) {
            return true;
        }

        for (UserEntity userEntity : users) {
            if (userEntity.getUserId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasUser() {
        return (users != null && users.size() > 0);
    }
}
