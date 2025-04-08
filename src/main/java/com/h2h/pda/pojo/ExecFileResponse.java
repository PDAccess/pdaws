package com.h2h.pda.pojo;

import com.h2h.pda.entity.ExecFile;

import java.util.List;

public class ExecFileResponse {

    private List<ExecFile> logs;
    private int totalPages;

    public List<ExecFile> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecFile> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
