package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecAuth;

import java.util.List;

public class ExecAuthResponse {

    private List<ExecAuth> logs;
    private int totalPages;

    public List<ExecAuth> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecAuth> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
