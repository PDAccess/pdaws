package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class CredentialManagerResponse {
    boolean result;
    String message;
    Timestamp runTime;

    public boolean isResult() {
        return result;
    }

    public CredentialManagerResponse setResult(boolean result) {
        this.result = result;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CredentialManagerResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Timestamp getRunTime() {
        return runTime;
    }

    public CredentialManagerResponse setRunTime(Timestamp runTime) {
        this.runTime = runTime;
        return this;
    }
}
