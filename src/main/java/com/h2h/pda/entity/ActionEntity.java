package com.h2h.pda.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="plogs")
public class ActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plogsSequenceGenerator")
    @SequenceGenerator(name = "plogsSequenceGenerator", sequenceName = "plogs_table_sequence", initialValue = 1, allocationSize = 1)
    private int id;

    @Column(name = "session_id")
    private int sessionId;

    @Column(name = "action_time")
    private Timestamp actionTime;

    @Column(name = "proxy_action")
    @JsonProperty("proxy_action")
    private String proxyAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private SessionEntity sessionEntity;

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }

    public void setSessionEntity(SessionEntity sessionEntity) {
        this.sessionEntity = sessionEntity;
    }

    public ActionEntity() {
    }

    public ActionEntity( String proxyAction, SessionEntity sessionEntity) {
        this.proxyAction = proxyAction;
        this.sessionEntity = sessionEntity;
    }

    public String getProxyAction() {
        return proxyAction;
    }

    public void setProxyAction(String proxyAction) {
        this.proxyAction = proxyAction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}

