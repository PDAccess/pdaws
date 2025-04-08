package com.h2h.pda.pojo;

import com.h2h.pda.entity.AgentStatusEntity;

import java.util.List;

public class ExecAgentStatusResponse {

    private List<AgentStatusEntity> logs;
    private int totalPages;

    public List<AgentStatusEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<AgentStatusEntity> logs) {
        this.logs = logs;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
