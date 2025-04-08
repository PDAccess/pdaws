package com.h2h.pda.pojo.user;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum UserRole {
    ADMIN(1, "Admin"),
    USER(2, "User"),
    SYSTEM(3, "System"),
    UNKNOWN_ROLE(3, "unknown");

    private final int i;
    private final String name;

    UserRole(int i, String name) {
        this.i = i;
        this.name = name;
    }

    public static UserRole of(String name) {
        return Stream.of(UserRole.values())
                .filter(p -> p.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(UNKNOWN_ROLE);
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Deprecated
    public boolean isEqual(String name) {
        return this.name.equals(name);
    }

}