package com.h2h.pda.pojo.service;


import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum ServiceOs {
    UBUNTU(1, "ubuntu"),
    REDHAT(2, "redhat"),
    DEBIAN(3, "debian"),
    CENTOS(4, "centos"),
    WINDOWS(5, "windows"),
    UNIX(6, "unix"),
    LINUX(7, "linux"),
    SOLARIS(8, "solaris"),
    CISCO(9, "cisco"),
    ORACLE_LINUX(10, "oracle_linux"),
    SUSE(11, "suse"),
    LDAP(12, "ldap"),
    ACTIVE_DIRECTORY(13, "ad"),
    ZTE(14, "zte"),
    OTHER(15, "other"),
    UNKNOWN_SERVICE(1000, "unknown");

    private final int i;
    private final String name;

    ServiceOs(int i, String name) {
        this.i = i;
        this.name = name;
    }

    public static ServiceOs of(Integer id) {
        return Stream.of(ServiceOs.values())
                .filter(p -> p.i == id)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getLogoName() {
        return this.name;
    }

    @JsonValue
    public Integer getIntValue() {
        return i;
    }
}
