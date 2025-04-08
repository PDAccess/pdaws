package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class PlaybookPlayInfo {

    @JsonProperty("installer_id")
    private Integer installerId;

    @JsonProperty("process_id")
    private Integer processId;

    @JsonProperty("history_id")
    private Integer historyId;

    public PlaybookPlayInfo(Map<String,String> json){
        this.installerId = Integer.parseInt(json.get("installerId"));
        this.processId = Integer.parseInt(json.get("processId"));
        this.historyId = Integer.parseInt(json.get("historyId"));
    }

    public Integer getInstallerId() {
        return installerId;
    }

    public void setInstallerId(Integer installerId) {
        this.installerId = installerId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }
}
