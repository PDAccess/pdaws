package com.h2h.pda.pojo.vault;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private List<String> keys;

    @JsonProperty("keys_base64")
    private List<String> keysBase64;

    @JsonProperty("root_token")
    private String rootToken;
    private List<String> errors;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getKeysBase64() {
        return keysBase64;
    }

    public void setKeysBase64(List<String> keysBase64) {
        this.keysBase64 = keysBase64;
    }

    public String getRootToken() {
        return rootToken;
    }

    public void setRootToken(String rootToken) {
        this.rootToken = rootToken;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
