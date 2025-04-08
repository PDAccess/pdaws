package com.h2h.pda.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.StringJoiner;

@Entity
@Table(name = "exec_trace_data")
public class ExecTrace implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execTraceDataSequenceGenerator")
    @SequenceGenerator(name = "execTraceDataSequenceGenerator", sequenceName = "exec_trace_data_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    private String host;
    @Column(name = "user_name")
    private String user;
    @Column(name = "user_id")
    private String userId;
    private String command;
    private String params;
    @Column(name = "exec_time")
    private String time;
    @Column(name = "exec_timestamp")
    private Timestamp execTimestamp;
    @Column(name = "groupid")
    @Deprecated
    private String groupid;
    @Column(name = "serviceid")
    private String serviceId;
    @Column(name = "report_time")
    private Timestamp reportTime;
    @Column(name = "login_user")
    private String loginUser;
    @Column(name = "login_address")
    private String loginAddress;
    @Column(name = "login_time")
    private Timestamp loginTime;
    @Column(name = "login_terminal")
    private String loginTerminal;
    @Column(name = "username")
    private String username;
    @Column(name = "e_username")
    private String eUsername;
    @Column(name = "server_hostname")
    private String serverHostname;
    @Column(name = "client_hostname")
    private String clientHostname;
    @Column(name = "ppid")
    private Integer ppid;
    @Column(name = "pgid")
    private Integer pgid;
    @Column(name = "psid")
    private Integer psid;
    @Column(name = "tgid")
    private Integer tgid;

    public ExecTrace() {
        // Constructor
    }

    public long getId() {
        return id;
    }

    public ExecTrace setId(long id) {
        this.id = id;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ExecTrace setUser(String user) {
        this.user = user;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ExecTrace setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public ExecTrace setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ExecTrace setHost(String host) {
        this.host = host;
        return this;
    }

    public String getParams() {
        return params;
    }

    public ExecTrace setParams(String params) {
        this.params = params;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ExecTrace setTime(String time) {
        this.time = time;
        return this;
    }

    @Deprecated
    public String getGroupid() {
        return groupid;
    }

    @Deprecated
    public ExecTrace setGroupid(String groupid) {
        this.groupid = groupid;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public ExecTrace setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExecTrace.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("host='" + host + "'")
                .add("user='" + user + "'")
                .add("userId='" + userId + "'")
                .add("command='" + command + "'")
                .add("params='" + params + "'")
                .add("time='" + time + "'")
                .add("serviceid='" + serviceId + "'")
                .add("groupid='" + groupid + "'")
                .toString();
    }

    public Timestamp getReportTime() {
        return reportTime;
    }

    public ExecTrace setReportTime(Timestamp reportTime) {
        this.reportTime = reportTime;
        return this;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public ExecTrace setLoginUser(String loginUser) {
        this.loginUser = loginUser;
        return this;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public ExecTrace setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
        return this;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public ExecTrace setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    public String getLoginTerminal() {
        return loginTerminal;
    }

    public void setLoginTerminal(String loginTerminal) {
        this.loginTerminal = loginTerminal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String geteUsername() {
        return eUsername;
    }

    public void seteUsername(String eUsername) {
        this.eUsername = eUsername;
    }

    public Timestamp getExecTimestamp() {
        return execTimestamp;
    }

    public void setExecTimestamp(Timestamp execTimestamp) {
        this.execTimestamp = execTimestamp;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    public String getClientHostname() {
        return clientHostname;
    }

    public void setClientHostname(String clientHostname) {
        this.clientHostname = clientHostname;
    }

    public Integer getPpid() {
        return ppid;
    }

    public void setPpid(Integer ppid) {
        this.ppid = ppid;
    }

    public Integer getPgid() {
        return pgid;
    }

    public void setPgid(Integer pgid) {
        this.pgid = pgid;
    }

    public Integer getPsid() {
        return psid;
    }

    public void setPsid(Integer psid) {
        this.psid = psid;
    }

    public Integer getTgid() {
        return tgid;
    }

    public void setTgid(Integer tgid) {
        this.tgid = tgid;
    }
}
