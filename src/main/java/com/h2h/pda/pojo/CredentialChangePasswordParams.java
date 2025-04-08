package com.h2h.pda.pojo;

public class CredentialChangePasswordParams {

    private String credentialId;
    private String password;

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
