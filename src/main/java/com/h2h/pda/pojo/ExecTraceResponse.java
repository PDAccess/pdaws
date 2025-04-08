package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecTrace;

import java.util.List;

public class ExecTraceResponse {

    private List<ExecTrace> logs;
    private int totalPages;

    public List<ExecTrace> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecTrace> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
