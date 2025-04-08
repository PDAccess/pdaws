package com.h2h.pda.pojo.group;

import java.util.stream.Stream;

public enum GroupProperties {
    LDAP_DN("ldap-dn"),
    MAXIMUM_SESSION("maximum-session"),
    IDLE_TIMEOUT("idle-timeout");

    String property;

    GroupProperties(String property) {
        this.property = property;
    }

    public static GroupProperties of(String property) {
        return Stream.of(GroupProperties.values())
                .filter(p -> p.property.equals(property))
                .findFirst()
                .orElseThrow(() -> makeException(property));
    }

    private static IllegalArgumentException makeException(String property) {
        return new IllegalArgumentException(String.format("There is no parameter for: %s", property));
    }

    public static GroupProperties ofElseThrow(String property) {
        return Stream.of(GroupProperties.values())
                .filter(p -> p.property.equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getPropertyString() {
        return property;
    }
}