package com.h2h.pda.pojo.user;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum UserShell {
    SH("/bin/sh"),
    BASH("/bin/bash"),
    KSH("/bin/ksh"),
    PDAX_SH("/usr/bin/pdax/sh"),
    PDAX_BASH("/usr/bin/pdax/bash"),
    PDAX_KSH("/usr/bin/pdax/ksh"),
    UNKNOWN_SHELL("");

    private final String name;

    UserShell(String name) {
        this.name = name;
    }

    public static UserShell of(String name) {
        return Stream.of(UserShell.values())
                .filter(p -> p.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(UNKNOWN_SHELL);
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
