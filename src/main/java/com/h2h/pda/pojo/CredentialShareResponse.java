package com.h2h.pda.pojo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "credentials")
public class CredentialShareResponse implements EntityToDTO<CredentialShareResponse, CredentialParams> {

    private String credentialId;
    private String username;
    private String password;
    private String key;
    private String ppKey;
    private List<KeyValue> keyValues;

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

    public String getPpKey() {
        return ppKey;
    }

    public void setPpKey(String ppKey) {
        this.ppKey = ppKey;
    }

    @Override
    public CredentialShareResponse wrap(CredentialParams entity) {
        setCredentialId(entity.getCredentialId());
        setKey(entity.getKey());
        setKeyValues(entity.getKeyValues());
        setPassword(entity.getPassword());
        setPpKey(entity.getPpKey());
        setUsername(entity.getUsername());
        return this;
    }

    @Override
    public CredentialParams unWrap() {
        return null;
    }
}
