package com.h2h.pda.pojo.vault;

import java.io.Serializable;

public class CredentialCounter implements Serializable {

    private long all;
    private long yours;

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }

    public long getYours() {
        return yours;
    }

    public void setYours(long yours) {
        this.yours = yours;
    }
}
