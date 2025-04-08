package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CredentialRequestResponseParams {

    @JsonProperty("is_approved")
    private boolean isApproved;

    @JsonProperty("credential_id")
    private String credentialId;

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }
}
