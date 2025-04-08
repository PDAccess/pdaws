package com.h2h.pda.jwt.providers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LdapAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public LdapAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
