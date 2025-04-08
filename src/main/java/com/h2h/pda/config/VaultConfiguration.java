package com.h2h.pda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

import java.net.URI;

@Component
public class VaultConfiguration {

    @Value("${vault.endpoint}")
    private String vaultEndpoint;

    @Bean
    VaultTemplate template() {
        return new VaultTemplate(VaultEndpoint.from(URI.create(vaultEndpoint)),
                new SimpleClientHttpRequestFactory(), () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = (String) authentication.getPrincipal();
            return new TokenAuthentication(principal).login();
        });
    }
}
