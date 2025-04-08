package com.h2h.pda.pojo;

public class PlaybookStatus {

    private Boolean finished;
    private String result;

    public PlaybookStatus(Boolean finished, String result) {
        this.finished = finished;
        this.result = result;
    }

    public PlaybookStatus() {
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
