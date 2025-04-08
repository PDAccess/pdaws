package com.h2h.pda.pojo.service;

import java.util.Optional;
import java.util.stream.Stream;

public enum ServiceMeta {
    DATABASE("D"),
    TERMINAL("T"),
    WEBAPP("W"),
    LDAP("L"),
    PDA("PDA");

    String property;

    ServiceMeta(String property) {
        this.property = property;
    }

    public static Optional<ServiceMeta> of(String property) {
        return Stream.of(ServiceMeta.values())
                .filter(p -> p.property.equals(property))
                .findFirst();
    }

    public static ServiceMeta ofElseThrow(String property) {
        return Stream.of(ServiceMeta.values())
                .filter(p -> p.property.equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getPropertyString() {
        return property;
    }
}
