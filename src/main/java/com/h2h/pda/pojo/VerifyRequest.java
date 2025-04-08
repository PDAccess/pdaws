package com.h2h.pda.pojo;

public class VerifyRequest {
    String hostname;
    int port;
    String proto;
    String username;
    String password;
    String newpassword;

    public String getHostname() {
        return hostname;
    }

    public VerifyRequest setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public int getPort() {
        return port;
    }

    public VerifyRequest setPort(int port) {
        this.port = port;
        return this;
    }

    public String getProto() {
        return proto;
    }

    public VerifyRequest setProto(String proto) {
        this.proto = proto;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public VerifyRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public VerifyRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public VerifyRequest setNewpassword(String newpassword) {
        this.newpassword = newpassword;
        return this;
    }
}
