package com.h2h.pda.pojo.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VaultLdapConfig {

    @JsonProperty("url")
    private String url;

    @JsonProperty("insecure_tls")
    private boolean insecureTLS;

    @JsonProperty("starttls")
    private boolean startTLS;

    @JsonProperty("bindpass")
    private String bindPass;

    @JsonProperty("binddn")
    private String bindDN;

    @JsonProperty("userdn")
    private String userDN;

    @JsonProperty("groupdn")
    private String groupDN;

    @JsonProperty("groupfilter")
    private String groupFilter;

    @JsonProperty("groupattr")
    private String groupAttr;

    @JsonProperty("discoverdn")
    private Boolean discoverDN;

    @JsonProperty("userattr")
    private String userAttr;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getInsecureTLS() {
        return insecureTLS;
    }

    public void setInsecureTLS(boolean insecureTLS) {
        this.insecureTLS = insecureTLS;
    }

    public boolean getStartTLS() {
        return startTLS;
    }

    public void setStartTLS(boolean startTLS) {
        this.startTLS = startTLS;
    }

    public String getBindPass() {
        return bindPass;
    }

    public void setBindPass(String bindPass) {
        this.bindPass = bindPass;
    }

    public String getBindDN() {
        return bindDN;
    }

    public void setBindDN(String bindDN) {
        this.bindDN = bindDN;
    }

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

    public Boolean getDiscoverDN() {
        return discoverDN;
    }

    public void setDiscoverDN(Boolean discoverDN) {
        this.discoverDN = discoverDN;
    }

    public String getUserAttr() {
        return userAttr;
    }

    public void setUserAttr(String userAttr) {
        this.userAttr = userAttr;
    }
}
