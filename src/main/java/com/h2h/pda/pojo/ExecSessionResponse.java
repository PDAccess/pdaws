package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecSessionEntity;

import java.util.List;

public class ExecSessionResponse {

    private List<ExecSessionEntity> logs;
    private int totalPages;

    public List<ExecSessionEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecSessionEntity> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
