package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManageAccountParams {

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("credential_id")
    private String credentialId;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

}
