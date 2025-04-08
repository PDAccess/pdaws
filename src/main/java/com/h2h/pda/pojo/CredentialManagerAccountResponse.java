package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class CredentialManagerAccountResponse {

    private boolean result;
    private String message;
    private Timestamp runTime;
    private LocalAccount[] accounts;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getRunTime() {
        return runTime;
    }

    public void setRunTime(Timestamp runTime) {
        this.runTime = runTime;
    }

    public LocalAccount[] getAccounts() {
        return accounts;
    }

    public void setAccounts(LocalAccount[] accounts) {
        this.accounts = accounts;
    }
}
