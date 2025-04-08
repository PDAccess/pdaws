package com.h2h.pda.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "sessions")
@Deprecated
public class AuthSessionEntity extends BaseEntity {

    @Id
    private int id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "user_name", nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", referencedColumnName = "username", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public AuthSessionEntity(){}

    public AuthSessionEntity(int id, String ipAddress, Timestamp updatedAt, Timestamp createdAt, String userAgent, String username) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.userAgent = userAgent;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

