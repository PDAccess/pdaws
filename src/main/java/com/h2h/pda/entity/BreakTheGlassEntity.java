package com.h2h.pda.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "breaktheglass")
public class BreakTheGlassEntity {

    @Id
    @Column(name = "breakid")
    private String breakId;

    @Column(name = "userid")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private UserEntity userEntity;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    private CredentialEntity credentialEntity;

    @Column(name = "checked_time")
    private Timestamp checkedTime;

    @Column(name = "checkout_time")
    private Timestamp checkoutTime;

    @Deprecated
    @Column(name = "serviceid")
    private String serviceid;

    @Deprecated
    @Column(name = "servicename")
    private String servicename;

    @Column(name = "created_at")
    @Deprecated
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "connection_user", referencedColumnName = "id")
    @Deprecated
    private ConnectionUserEntity connectionUserEntity;

    @Column(name = "checked")
    @Deprecated
    private Boolean checked;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_share")
    private boolean isShare;

    public BreakTheGlassEntity() {
        // Constructor
    }


    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakid) {
        this.breakId = breakid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public ConnectionUserEntity getConnectionUserEntity() {
        return connectionUserEntity;
    }

    public void setConnectionUserEntity(ConnectionUserEntity connectionUserEntity) {
        this.connectionUserEntity = connectionUserEntity;
    }

    public Timestamp getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(Timestamp checkedTime) {
        this.checkedTime = checkedTime;
    }

    public CredentialEntity getCredentialEntity() {
        return credentialEntity;
    }

    public BreakTheGlassEntity setCredentialEntity(CredentialEntity credentialEntity) {
        this.credentialEntity = credentialEntity;
        return this;
    }

    public Timestamp getCheckoutTime() {
        return checkoutTime;
    }

    public BreakTheGlassEntity setCheckoutTime(Timestamp checkoutTime) {
        this.checkoutTime = checkoutTime;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }
}
