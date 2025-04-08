package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BreakParams {

    @JsonProperty("credential_id")
    private String credentialId;

    @JsonProperty("reason")
    private String reason;

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
