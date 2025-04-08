package com.h2h.pda.entity;

import com.h2h.pda.pojo.user.MfaProviders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "mfa")
public class MfaEntity {

    @Id
    private String username;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "google_authenticator")
    private Boolean googleAuthenticator;
    private Boolean sms;
    private Boolean email;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_checked_at")
    private Timestamp lastCheckedAt;

    public MfaEntity() {
        googleAuthenticator = false;
        sms = false;
        email =  false;
    }

    public MfaEntity(String username, String secretKey, Timestamp createdAt) {
        this();
        this.username = username;
        this.secretKey = secretKey;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Timestamp lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public Boolean getGoogleAuthenticator() {
        return googleAuthenticator;
    }

    public void setGoogleAuthenticator(Boolean googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    public Boolean getSms() {
        return sms;
    }

    public void setSms(Boolean sms) {
        this.sms = sms;
    }

    public Boolean getEmail() {
        return email;
    }

    public void setEmail(Boolean email) {
        this.email = email;
    }

    public Boolean isMfaEnabled() {
        return (googleAuthenticator != null && googleAuthenticator)  || (sms != null && sms) || (email != null && email);
    }

    public void setType(String type) {
        switch (MfaProviders.of(type)) {
            case EMAIL:
                email = true;
                googleAuthenticator = false;
                sms = false;
                break;
            case SMS:
                sms = true;
                googleAuthenticator = false;
                email = false;
                break;
            case GOOGLE_AUTHENTICATOR:
                googleAuthenticator = true;
                sms = false;
                email = false;
                break;
            default:
                break;
        }
    }

    public void enableMfa(String type) {
        switch (MfaProviders.of(type)) {
            case EMAIL:
                email = true;
                break;
            case SMS:
                sms = true;
                break;
            case GOOGLE_AUTHENTICATOR:
                googleAuthenticator = true;
                break;
            default:
                break;
        }
    }

    public void disableMfa(String type) {
        switch (MfaProviders.of(type)) {
            case EMAIL:
                email = false;
                break;
            case SMS:
                sms = false;
                break;
            case GOOGLE_AUTHENTICATOR:
                googleAuthenticator = false;
                break;
            default:
                break;
        }
    }
}