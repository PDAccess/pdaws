package com.h2h.pda.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "passwordreset")
public class PasswordResetEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "user_id")
    @Fetch(FetchMode.JOIN)
    private UserEntity userEntity;

    @Column(name="created_at")
    private Timestamp requestedAt;

    public PasswordResetEntity() {}

    public PasswordResetEntity(UserEntity userEntity, Boolean isApproved) {
        this.userEntity = userEntity;
        this.isApproved = isApproved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }
}
