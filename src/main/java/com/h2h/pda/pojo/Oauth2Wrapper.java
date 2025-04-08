package com.h2h.pda.pojo;

public class Oauth2Wrapper {
    private String callbackUrl;
    private String name;
    private Integer trusted;
    private String scopes;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTrusted() {
        return trusted;
    }

    public void setTrusted(Integer trusted) {
        this.trusted = trusted;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }
}
