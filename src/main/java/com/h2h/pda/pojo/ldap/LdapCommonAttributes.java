package com.h2h.pda.pojo.ldap;

import org.springframework.beans.factory.annotation.Value;

public class LdapCommonAttributes {

    @Value("${ldap.common.name}")
    private String name;

    @Value("${ldap.common.distinguishedName}")
    private String distinguishedName;

    @Value("${ldap.common.objectClass}")
    private String objectClass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }
}
