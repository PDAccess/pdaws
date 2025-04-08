package com.h2h.pda.pojo;

import java.io.Serializable;

public class ProxyError implements Serializable {
    private String message;

    public ProxyError(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
