package com.h2h.pda.pojo;

public class AuthUserPass {
    private String username;
    private String password;
    private String policy;
    private String rootToken;

    public String getUsername() {
        return username;
    }

    public AuthUserPass setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthUserPass setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPolicy() {
        return policy;
    }

    public AuthUserPass setPolicy(String policy) {
        this.policy = policy;
        return this;
    }

    public String getRootToken() {
        return rootToken;
    }

    public AuthUserPass setRootToken(String rootToken) {
        this.rootToken = rootToken;
        return this;
    }
}
