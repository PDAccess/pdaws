package com.h2h.pda.pojo;

public class Credential {
    public static final String SECRET_INVENTORY = "secret/inventory/";
    public static final String SECRET_CREDENTIAL = "secret/inventory/credentials/";

    private Integer id;
    private String password;
    private String username;
    private String key;
    private String ppKey;
    private String passphrase;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
