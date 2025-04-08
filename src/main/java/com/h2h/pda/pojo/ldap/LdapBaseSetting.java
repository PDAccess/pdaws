package com.h2h.pda.pojo.ldap;

public class LdapBaseSetting {

    private String url;
    private String base;
    private String userDn;
    private String password;
    private Boolean startTLS;
    private Boolean insecureTLS;
    private String serviceId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUserDn() {
        return userDn;
    }

    public void setUserDn(String userDn) {
        this.userDn = userDn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStartTLS() {
        return startTLS;
    }

    public void setStartTLS(Boolean startTLS) {
        this.startTLS = startTLS;
    }

    public Boolean getInsecureTLS() {
        return insecureTLS;
    }

    public void setInsecureTLS(Boolean insecureTLS) {
        this.insecureTLS = insecureTLS;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
