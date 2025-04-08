package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "exec_shell_sessions")
public class ExecShellSessionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execShellSessionsTableSequenceGenerator")
    @SequenceGenerator(name = "execShellSessionsTableSequenceGenerator", sequenceName = "exec_shell_sessions_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "username")
    private String username;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "e_username")
    private String eUsername;

    @Column(name = "e_user_id")
    private Integer eUserId;

    @Column(name = "service_id")
    private String serviceId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }


    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String geteUsername() {
        return eUsername;
    }

    public void seteUsername(String eUsername) {
        this.eUsername = eUsername;
    }

    public Integer geteUserId() {
        return eUserId;
    }

    public void seteUserId(Integer eUserId) {
        this.eUserId = eUserId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
