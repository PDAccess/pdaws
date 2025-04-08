package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecShellSessionEntity;

import java.util.List;

public class ExecShellSessionResponse {

    private List<ExecShellSessionEntity> logs;
    private int totalPages;

    public List<ExecShellSessionEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecShellSessionEntity> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
