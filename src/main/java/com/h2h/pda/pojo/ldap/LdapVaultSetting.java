package com.h2h.pda.pojo.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapVaultSetting {

    @JsonProperty("user_dn")
    private String userDN;

    @JsonProperty("group_dn")
    private String groupDN;

    @JsonProperty("group_filter")
    private String groupFilter;

    @JsonProperty("group_attr")
    private String groupAttr;

    @JsonProperty("user_attr")
    private String userAttr;

    @JsonProperty("discover_dn")
    private Boolean discoverDN;

    @JsonProperty("ldap_auth_status")
    private String ldapAuthStatus;

    public String getUserDN() {
        return userDN;
    }

    public void setUserDN(String userDN) {
        this.userDN = userDN;
    }

    public String getGroupDN() {
        return groupDN;
    }

    public void setGroupDN(String groupDN) {
        this.groupDN = groupDN;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public String getGroupAttr() {
        return groupAttr;
    }

    public void setGroupAttr(String groupAttr) {
        this.groupAttr = groupAttr;
    }

    public String getUserAttr() {
        return userAttr;
    }

    public void setUserAttr(String userAttr) {
        this.userAttr = userAttr;
    }

    public Boolean getDiscoverDN() {
        return discoverDN;
    }

    public void setDiscoverDN(Boolean discoverDN) {
        this.discoverDN = discoverDN;
    }

    public String getLdapAuthStatus() {
        return ldapAuthStatus;
    }

    public void setLdapAuthStatus(String ldapAuthStatus) {
        this.ldapAuthStatus = ldapAuthStatus;
    }
}
