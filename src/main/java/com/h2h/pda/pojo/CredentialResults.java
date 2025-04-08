package com.h2h.pda.pojo;

import java.sql.Timestamp;

public class CredentialResults {
    boolean result;
    String message;
    Timestamp runTime;

    public boolean getResult() {
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
}
