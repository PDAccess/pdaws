package com.h2h.pda.pojo.ldap;

public class LdapGroup {

    private String name;
    private String description;
    private String rdn;
    private String distinguishedName;
    private String parentGroup;

    public String getName() {
        return name;
    }

    public LdapGroup setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LdapGroup setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getRdn() {
        return rdn;
    }

    public void setRdn(String rdn) {
        this.rdn = rdn;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(String parentGroup) {
        this.parentGroup = parentGroup;
    }
}
