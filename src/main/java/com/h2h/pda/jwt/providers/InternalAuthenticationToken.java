package com.h2h.pda.jwt.providers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public final class InternalAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String serviceId;

    public InternalAuthenticationToken(Object principal, Object credentials, String serviceId) {
        super(principal, credentials);
        setServiceId(serviceId);
    }

    public String getServiceId() {
        return serviceId;
    }

    public InternalAuthenticationToken setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }
}
