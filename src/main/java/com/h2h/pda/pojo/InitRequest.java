package com.h2h.pda.pojo;

import java.util.List;

public class InitRequest {
    private String rootToken;
    private List<String> keys;

    public InitRequest() {
    }

    public InitRequest(String rootToken, List<String> keys) {
        this.rootToken = rootToken;
        this.keys = keys;
    }

    public String getRootToken() {
        return rootToken;
    }

    public void setRootToken(String rootToken) {
        this.rootToken = rootToken;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
