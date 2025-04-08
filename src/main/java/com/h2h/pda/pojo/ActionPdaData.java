package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class ActionPdaData {
    private Integer sessionId;
    private String actionPayload;
    private Timestamp actionTime;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getActionMessage() {
        return actionPayload;
    }

    public void setActionMessage(String actionMessage) {
        this.actionPayload = actionMessage;
    }

    public Timestamp getActionTime() {
        return actionTime;
    }

    public void setActionTime(Timestamp actionTime) {
        this.actionTime = actionTime;
    }
}
