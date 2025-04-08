package com.h2h.pda.pojo.vault;

import java.io.Serializable;

public class CredentialRequestCounter implements Serializable {

    private long waited;
    private long approved;
    private long notApproved;

    public long getWaited() {
        return waited;
    }

    public void setWaited(long waited) {
        this.waited = waited;
    }

    public long getApproved() {
        return approved;
    }

    public void setApproved(long approved) {
        this.approved = approved;
    }

    public long getNotApproved() {
        return notApproved;
    }

    public void setNotApproved(long notApproved) {
        this.notApproved = notApproved;
    }
}
