package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "exec_file_data")
public class ExecFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execFileDataSequenceGenerator")
    @SequenceGenerator(name = "execFileDataSequenceGenerator", sequenceName = "exec_file_data_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    private String host;
    @Column(name = "user_name")
    private String user;
    @Column(name = "user_id")
    private String userId;
    private String path;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_action")
    private String action;
    @Column(name = "exec_time")
    private Timestamp time;
    @Column(name = "group_id")
    @Deprecated
    private String groupId;
    @Column(name = "service_id")
    private String serviceId;
    @Column(name = "report_time")
    private Timestamp reportTime;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
}
