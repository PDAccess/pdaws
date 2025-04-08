package com.h2h.pda.pojo.ldap;

import org.springframework.beans.factory.annotation.Value;

public class LdapUserAttributes {

    @Value("${ldap.user.objectClass}")
    private String objectClass;

    @Value("${ldap.user.username}")
    private String username;

    @Value("${ldap.user.firstName}")
    private String firstName;

    @Value("${ldap.user.lastName}")
    private String lastName;

    @Value("${ldap.user.mail}")
    private String mail;

    @Value("${ldap.user.telephoneNumber}")
    private String telephoneNumber;

    @Value("${ldap.user.distinguishedName}")
    private String distinguishedName;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }
}
