package com.h2h.pda.pojo.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapPanelSetting {

    @JsonProperty("ldap_url")
    private String ldapUrl;

    @JsonProperty("ldap_port")
    private String ldapPort;

    @JsonProperty("base_dn")
    private String baseDN;

    @JsonProperty("bind_dn")
    private String userDN;

    @JsonProperty("bind_pass")
    private String password;

    @JsonProperty("group_ou")
    private String groupOU;

    @JsonProperty("user_ou")
    private String userOU;

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getLdapPort() {
        return ldapPort;
    }

    public void setLdapPort(String ldapPort) {
        this.ldapPort = ldapPort;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getUserDN() {
        return userDN;
    }

    public void setUserDN(String userDN) {
        this.userDN = userDN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupOU() {
        return groupOU;
    }

    public void setGroupOU(String groupOU) {
        this.groupOU = groupOU;
    }

    public String getUserOU() {
        return userOU;
    }

    public void setUserOU(String userOU) {
        this.userOU = userOU;
    }

}
