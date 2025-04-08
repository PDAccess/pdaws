package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlaybookPayload {

    @JsonProperty("content")
    private String ymlContent;

    @JsonProperty("installer_id")
    private int installerId;

    @JsonProperty("history_id")
    private int historyId;

    private List<PlaybookHost> hosts;

    public String getYmlContent() {
        return ymlContent;
    }

    public void setYmlContent(String ymlContent) {
        this.ymlContent = ymlContent;
    }

    public List<PlaybookHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<PlaybookHost> hosts) {
        this.hosts = hosts;
    }

    public int getInstallerId() {
        return installerId;
    }

    public void setInstallerId(int installerId) {
        this.installerId = installerId;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
}
