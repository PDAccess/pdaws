package com.h2h.pda.pojo;

import com.h2h.pda.entity.ConnectionUserEntity;
import org.springframework.util.StringUtils;

import java.util.List;

public class CredentialParams {

    private String credentialId;
    private String username;
    private String password;
    private String key;
    private String ppKey;
    private List<KeyValue> keyValues;
    private ConnectionUserEntity account;
    private String groupId;
    private List<String> services;
    private boolean check;
    private String parentCredentialId;

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<KeyValue> getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(List<KeyValue> keyValues) {
        this.keyValues = keyValues;
    }

    public ConnectionUserEntity getAccount() {
        return account;
    }

    public void setAccount(ConnectionUserEntity account) {
        this.account = account;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean hasValidParams() {
        return StringUtils.hasLength(username) && (StringUtils.hasLength(parentCredentialId) || StringUtils.hasLength(password) || StringUtils.hasLength(key));
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getPpKey() {
        return ppKey;
    }

    public void setPpKey(String ppKey) {
        this.ppKey = ppKey;
    }

    public String getParentCredentialId() {
        return parentCredentialId;
    }

    public void setParentCredentialId(String parentCredentialId) {
        this.parentCredentialId = parentCredentialId;
    }

    public String toString() {
        return String.format("Username: %s\n" +
                "Password: %s\n" +
                "Key: %s\n", this.username, this.password, this.key);
    }
}
