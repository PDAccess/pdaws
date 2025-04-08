package com.h2h.pda.pojo;

public class AuthCredentials {

    String clientToken;
    String sessionId;

    public AuthCredentials() {
    }

    public AuthCredentials(String clientToken, String sessionId) {
        this.clientToken = clientToken;
        this.sessionId = sessionId;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
