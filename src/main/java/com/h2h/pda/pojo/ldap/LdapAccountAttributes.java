package com.h2h.pda.pojo.ldap;

import org.springframework.beans.factory.annotation.Value;

public class LdapAccountAttributes {

    @Value("${ldap.account.objectClass}")
    private String objectClass;

    @Value("${ldap.account.username}")
    private String username;

    @Value("${ldap.account.password}")
    private String password;

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
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

}
