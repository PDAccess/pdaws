package com.h2h.pda.pojo.ldap;

import org.springframework.beans.factory.annotation.Value;

public class LdapGroupAttributes {

    @Value("${ldap.group.objectClass}")
    private String objectClass;

    @Value("${ldap.group.name}")
    private String name;

    @Value("${ldap.group.description}")
    private String description;

    @Value("${ldap.group.distinguishedName}")
    private String distinguishedName;

    @Value("${ldap.group.memberAttribute}")
    private String memberAttribute;

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

    public String getMemberAttribute() {
        return memberAttribute;
    }

    public void setMemberAttribute(String memberAttribute) {
        this.memberAttribute = memberAttribute;
    }
}
