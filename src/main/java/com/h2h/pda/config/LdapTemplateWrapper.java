package com.h2h.pda.config;

import com.h2h.pda.pojo.ldap.LdapSetting;
import com.h2h.pda.service.api.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultTlsDirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.vault.support.VaultResponseSupport;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static com.h2h.pda.pojo.Credential.SECRET_INVENTORY;

public class LdapTemplateWrapper {
    private Logger log = LoggerFactory.getLogger(LdapTemplateWrapper.class);

    private LdapTemplate ldapTemplate;
    private LdapContextSource contextSource;

    @Autowired
    VaultService vaultService;

    public LdapTemplate getLdapTemplate() throws Exception {
        if (ldapTemplate == null) {
            VaultResponseSupport<LdapSetting> vaultResponseSupport = vaultService.doWithVaultUsingRootTokenAndTemplate(callback -> {
                VaultResponseSupport<LdapSetting> vaultResponseSupport2 = callback.read(SECRET_INVENTORY + 1234, LdapSetting.class);
                return vaultResponseSupport2;
            });

            if (vaultResponseSupport == null || vaultResponseSupport.getData() == null) {
                throw new Exception("LDAP settings were not saved correctly");
            }
            LdapSetting setting = vaultResponseSupport.getData();
            if (setting != null) {
                String url = (setting.getSsl() ? "ldaps://" : "ldap://") + setting.getHost() + ":" + setting.getPort();
                this.setLdapSettings(url, setting.getBaseDN(), setting.getBindDN(), setting.getBindPass(), setting.getStartTLS(), setting.getInsecureTLS());
            }
        }
        return ldapTemplate;
    }

    public LdapContextSource getLdapContextSource() {
        return contextSource;
    }

    public void setLdapSettings(String url, String base, String userDn, String password, boolean startTLS, boolean insecureTLS) throws Exception {
        this.contextSource = new LdapContextSource();
        contextSource.setUrl(url);
        contextSource.setBase(base);
        contextSource.setUserDn(userDn);
        contextSource.setPassword(password);

        if (startTLS) {
            DefaultTlsDirContextAuthenticationStrategy authenticationStrategy = new DefaultTlsDirContextAuthenticationStrategy();
            authenticationStrategy.setShutdownTlsGracefully(true);
            authenticationStrategy.setHostnameVerifier((hostname, session) -> true);
            if (insecureTLS) {
                authenticationStrategy.setSslSocketFactory(trustSelfSignedSSL());
            }
            contextSource.setAuthenticationStrategy(authenticationStrategy);
        }

        contextSource.afterPropertiesSet();

        this.ldapTemplate = new LdapTemplate(this.contextSource);
        this.ldapTemplate.setIgnorePartialResultException(true);
        this.ldapTemplate.afterPropertiesSet();
    }

    public SSLSocketFactory trustSelfSignedSSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);

            return ctx.getSocketFactory();
        } catch (Exception ex) {
            log.error("error while configuring ssl factory", ex);
            return null;
        }
    }
}
