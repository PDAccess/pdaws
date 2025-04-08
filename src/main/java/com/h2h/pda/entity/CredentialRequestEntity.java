package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "credential_requests")
public class CredentialRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentialRequestsSequenceGenerator")
    @SequenceGenerator(name = "credentialRequestsSequenceGenerator", sequenceName = "credential_requests_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    private CredentialEntity credentialEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user", referencedColumnName = "user_id")
    private UserEntity requestingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responding_user", referencedColumnName = "user_id")
    private UserEntity respondingUser;

    @Column(name = "is_approval")
    private boolean isApproval;

    @Column(name = "requested_at")
    private Timestamp requestedAt;

    @Column(name = "responded_at")
    private Timestamp respondedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public void setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
    }

    public UserEntity getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(UserEntity requestingUser) {
        this.requestingUser = requestingUser;
    }

    public UserEntity getRespondingUser() {
        return respondingUser;
    }

    public void setRespondingUser(UserEntity respondingUser) {
        this.respondingUser = respondingUser;
    }

    public boolean isApproval() {
        return isApproval;
    }

    public void setApproval(boolean approval) {
        isApproval = approval;
    }

    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Timestamp getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Timestamp respondedAt) {
        this.respondedAt = respondedAt;
    }
}
