package com.h2h.pda.pojo;

public class Inventory {

    private String ipaddress;
    private String password;
    private String port;
    private String username;
    private String key;
    private String ppKey;
    private String passphrase;
    private String dbname;

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPpKey() {
        return ppKey;
    }

    public void setPpKey(String ppKey) {
        this.ppKey = ppKey;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "ipaddress='" + ipaddress + '\'' +
                ", password='" + password + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                '}';
    }


}
