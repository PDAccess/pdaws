package com.h2h.pda.service.impl;

import com.h2h.pda.config.LdapTemplateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class LdapUtil {
    private Logger log = LoggerFactory.getLogger(LdapServiceImpl.class);

    @Autowired
    LdapTemplateWrapper templateWrapper;

    public String getLdapAttributes(Attributes attributes, String attributeName) {
        try {
            Attribute attribute = attributes.get(attributeName);
            if (attribute == null) {
                return null;
            }

            return ((String) attribute.get());
        } catch (Exception exception) {
            log.error("error in get attributes", exception);
            return null;
        }
    }

    public String getRdn(String dn) {
        String rdn = "";
        if (dn != null) {
            rdn = dn.replaceAll("(?i),".concat(templateWrapper.getLdapContextSource().getBaseLdapPathAsString()), "");;
        }
        return rdn;
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
            ex.printStackTrace();
            return null;
        }
    }

}
