package com.h2h.pda.pojo.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapSetting {

    @JsonProperty("ldap_url")
    private String host;

    @JsonProperty("ldap_port")
    private String port;

    @JsonProperty("base_dn")
    private String baseDN;

    @JsonProperty("bind_dn")
    private String bindDN;

    @JsonProperty("bind_pass")
    private String bindPass;

    @JsonProperty("insecure_tls")
    private Boolean insecureTLS;

    @JsonProperty("start_tls")
    private Boolean startTLS;

    @JsonProperty("ssl")
    private Boolean ssl;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBindDN() {
        return bindDN;
    }

    public void setBindDN(String bindDN) {
        this.bindDN = bindDN;
    }

    public String getBindPass() {
        return bindPass;
    }

    public void setBindPass(String bindPass) {
        this.bindPass = bindPass;
    }

    public Boolean getInsecureTLS() {
        return insecureTLS;
    }

    public void setInsecureTLS(Boolean insecureTLS) {
        this.insecureTLS = insecureTLS;
    }

    public Boolean getStartTLS() {
        return startTLS;
    }

    public void setStartTLS(Boolean startTLS) {
        this.startTLS = startTLS;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }
}
