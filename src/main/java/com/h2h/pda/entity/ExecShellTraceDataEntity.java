package com.h2h.pda.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "exec_shell_trace_data")
public class ExecShellTraceDataEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "execShellTraceDataTableSequenceGenerator")
    @SequenceGenerator(name = "execShellTraceDataTableSequenceGenerator", sequenceName = "exec_shell_trace_data_table_sequence", initialValue = 1, allocationSize = 1)
    private long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "exec_time")
    private long execTime;

    @Column(name = "std_out")
    private String stdOut;

    @Column(name = "exec_command")
    private String execCommand;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "report_time")
    private Timestamp reportTime;

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

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    public String getStdOut() {
        return stdOut;
    }

    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    public String getExecCommand() {
        return execCommand;
    }

    public void setExecCommand(String execCommand) {
        this.execCommand = execCommand;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Timestamp getReportTime() {
        return reportTime;
    }

    public void setReportTime(Timestamp reportTime) {
        this.reportTime = reportTime;
    }
}
