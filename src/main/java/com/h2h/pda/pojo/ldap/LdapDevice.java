package com.h2h.pda.pojo.ldap;

public class LdapDevice {

    private String name;
    private String description;
    private String rdn;
    private String distinguishedName;

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
}
