package com.h2h.pda.pojo;

public class ConnectionParams {
    private String serverName;
    private int port;

    public ConnectionParams(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
    }

    public ConnectionParams() {
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
