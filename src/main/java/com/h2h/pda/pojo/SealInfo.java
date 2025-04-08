package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SealInfo {
    @JsonProperty("secret_shares")
    private int secretShares;
    @JsonProperty("secret_threshold")
    private int secretThreshold;

    public int getSecretShares() {
        return secretShares;
    }

    public SealInfo setSecretShares(int secretShares) {
        this.secretShares = secretShares;
        return this;
    }

    public int getSecretThreshold() {
        return secretThreshold;
    }

    public SealInfo setSecretThreshold(int secretThreshold) {
        this.secretThreshold = secretThreshold;
        return this;
    }
}
