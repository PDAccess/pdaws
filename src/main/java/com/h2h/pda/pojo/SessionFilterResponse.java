package com.h2h.pda.pojo;

import com.h2h.pda.entity.SessionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SessionFilterResponse {
    private List<SessionEntityWrapper> sessionEntities;
    private Integer sessionCount;

    public SessionFilterResponse() {
    }

    public SessionFilterResponse(List<SessionEntity> sessionEntities, Integer sessionCount) {
        this.sessionEntities = sessionEntities.stream().map(s -> new SessionEntityWrapper(s)).collect(Collectors.toList());
        this.sessionCount = sessionCount;
    }

    public List<SessionEntityWrapper> getSessionEntities() {
        return sessionEntities;
    }

    public void setSessionEntities(List<SessionEntityWrapper> sessionEntities) {
        this.sessionEntities = sessionEntities;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }
}
