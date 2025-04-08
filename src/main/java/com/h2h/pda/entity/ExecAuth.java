package com.h2h.pda.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "exec_auth_data")
public class ExecAuth implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execAuthDataSequenceGenerator")
    @SequenceGenerator(name = "execAuthDataSequenceGenerator", sequenceName = "exec_auth_data_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String function;
    private String flags;
    private String service;
    private String host;
    @Column(name = "user_name")
    private String user;
    private String tty;
    @Column(name = "exec_time")
    private Timestamp time;
    @Column(name = "group_id")
    @Deprecated
    private String groupId;
    @Column(name = "service_id")
    private String serviceId;
    @Column(name = "report_time")
    private Timestamp reportTime;
    @Column(name = "r_user")
    private String rUser;
    @Column(name = "r_host")
    private String rHost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTty() {
        return tty;
    }

    public void setTty(String tty) {
        this.tty = tty;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Timestamp getReportTime() {
        return reportTime;
    }

    public void setReportTime(Timestamp reportTime) {
        this.reportTime = reportTime;
    }

    @Deprecated
    public String getGroupId() {
        return groupId;
    }

    @Deprecated
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getrUser() {
        return rUser;
    }

    public void setrUser(String rUser) {
        this.rUser = rUser;
    }

    public String getrHost() {
        return rHost;
    }

    public void setrHost(String rHost) {
        this.rHost = rHost;
    }
}

