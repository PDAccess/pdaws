package com.h2h.pda.config;

import com.h2h.pda.pojo.ldap.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdapConfiguration {
    @Bean
    public LdapTemplateWrapper setUpLdapTemplate() {
        return new LdapTemplateWrapper();
    }

    @Bean
    public LdapGroupAttributes getLdapGroupAttributes() {
        return new LdapGroupAttributes();
    }

    @Bean
    public LdapUserAttributes getLdapUserAttributes() {
        return new LdapUserAttributes();
    }

    @Bean
    public LdapDeviceAttributes getLdapDeviceAttributes() {
        return new LdapDeviceAttributes();
    }

    @Bean
    public LdapCommonAttributes getLdapCommonAttributes() {
        return new LdapCommonAttributes();
    }

    @Bean
    public LdapAccountAttributes getLdapAccountAttributes() {
        return new LdapAccountAttributes();
    }
}
