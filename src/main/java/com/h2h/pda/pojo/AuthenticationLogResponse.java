package com.h2h.pda.pojo;

import com.h2h.pda.pojo.auth.AuthenticationAttemptEntityWrapper;

import java.util.List;

public class AuthenticationLogResponse {

    private List<AuthenticationAttemptEntityWrapper> logs;
    private int totalPages;

    public List<AuthenticationAttemptEntityWrapper> getLogs() {
        return logs;
    }

    public void setLogs(List<AuthenticationAttemptEntityWrapper> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
