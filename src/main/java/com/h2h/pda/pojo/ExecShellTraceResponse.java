package com.h2h.pda.pojo;


import com.h2h.pda.entity.ExecShellTraceDataEntity;

import java.util.List;

public class ExecShellTraceResponse {

    private List<ExecShellTraceDataEntity> logs;
    private int totalPages;

    public List<ExecShellTraceDataEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<ExecShellTraceDataEntity> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
