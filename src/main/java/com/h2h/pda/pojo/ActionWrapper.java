package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.h2h.pda.entity.ActionEntity;

import java.sql.Timestamp;

public class ActionWrapper implements EntityToDTO<ActionWrapper, ActionEntity> {
    @JsonProperty("session_id")
    private int sessionId;

    @JsonProperty("action_time")
    private Timestamp actionTime;

    @JsonProperty("proxy_action")
    private String proxyAction;

    private SessionEntityWrapper session;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getActionTime() {
        return actionTime;
    }

    public void setActionTime(Timestamp actionTime) {
        this.actionTime = actionTime;
    }

    public String getProxyAction() {
        return proxyAction;
    }

    public void setProxyAction(String proxyAction) {
        this.proxyAction = proxyAction;
    }

    public SessionEntityWrapper getSession() {
        return session;
    }

    public ActionWrapper setSession(SessionEntityWrapper session) {
        this.session = session;
        return this;
    }

    @Override
    public ActionWrapper wrap(ActionEntity entity) {
        setSessionId(entity.getSessionId());
        setActionTime(entity.getActionTime());
        setProxyAction(entity.getProxyAction());
        setSession(new SessionEntityWrapper(entity.getSessionEntity()));
        return this;
    }

    @Override
    public ActionEntity unWrap() {
        ActionEntity entity = new ActionEntity();
        entity.setSessionId(getSessionId());
        entity.setActionTime(getActionTime());
        entity.setProxyAction(getProxyAction());
        entity.setSessionEntity(getSession().unWrap());
        return entity;
    }
}
