package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "exec_sessions")
public class ExecSessionEntity {

    @Id
    @Column(name = "session_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execSessionsTableSequenceGenerator")
    @SequenceGenerator(name = "execSessionsTableSequenceGenerator", sequenceName = "exec_sessions_table_sequence", initialValue = 1, allocationSize = 1)
    private long sessionId;

    @Column(name = "login_terminal")
    private String loginTerminal;

    @Column(name = "session_start_time")
    private Timestamp sessionStartTime;

    @Column(name = "session_end_time")
    private Timestamp sessionEndTime;

    @Column(name = "remote_address")
    private String remoteAddress;

    @Column(name = "login_user")
    private String loginUser;

    @Column(name = "service_id")
    private String serviceId;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getLoginTerminal() {
        return loginTerminal;
    }

    public void setLoginTerminal(String loginTerminal) {
        this.loginTerminal = loginTerminal;
    }

    public Timestamp getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(Timestamp sessionTime) {
        this.sessionStartTime = sessionTime;
    }

    public Timestamp getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(Timestamp sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}
