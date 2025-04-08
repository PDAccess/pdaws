package com.h2h.pda.pojo;

import java.util.List;

public class ExecShellGroupSessionResponse {
    private List<ExecShellGroupSession> logs;
    private int totalPages;

    public List<ExecShellGroupSession> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecShellGroupSession> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
