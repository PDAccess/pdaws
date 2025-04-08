package com.h2h.pda.entity;

import com.h2h.pda.pojo.auth.LoginType;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "authentication_attempts")
public class AuthenticationAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authenticationAttemptsSequenceGenerator")
    @SequenceGenerator(name = "authenticationAttemptsSequenceGenerator", sequenceName = "authentication_attempts_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    private String username;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_type")
    @ColumnTransformer(read = "UPPER(login_type)")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String reason;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "attempted_at")
    private Timestamp attemptedAt;

    @Column(name = "service_id")
    private String serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "inventory_id", insertable = false, updatable = false)
    private ServiceEntity service;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Timestamp getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Timestamp attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ServiceEntity getService() {
        return service;
    }

    public AuthenticationAttemptEntity setService(ServiceEntity service) {
        this.service = service;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
