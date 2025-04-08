package com.h2h.pda.pojo.ldap;

import org.springframework.beans.factory.annotation.Value;

public class LdapDeviceAttributes {

    @Value("${ldap.device.objectClass}")
    private String objectClass;

    @Value("${ldap.device.name}")
    private String name;

    @Value("${ldap.device.description}")
    private String description;

    @Value("${ldap.device.distinguishedName}")
    private String distinguishedName;

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }
}
