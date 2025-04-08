package com.h2h.pda.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VaultAuthStatus {

    @JsonProperty("ldap/")
    private Object ldap;

    @JsonProperty("admin/")
    private Object admin;

    public Object getLdap() {
        return ldap;
    }

    public void setLdap(Object ldap) {
        this.ldap = ldap;
    }

    public Object getAdmin() {
        return admin;
    }

    public void setAdmin(Object admin) {
        this.admin = admin;
    }
}
