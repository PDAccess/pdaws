package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionDTO {
    @JsonProperty("inventoryId")
    private String inventoryId;

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }
}
