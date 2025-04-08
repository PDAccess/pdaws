package com.h2h.pda.pojo.session;

import java.util.Objects;
import java.util.stream.Stream;

public enum SessionType {
    SHELL, SESSION;

    public static SessionType of(String id) {
        Objects.requireNonNull(id);

        return Stream.of(SessionType.values())
                .filter(p -> id.equalsIgnoreCase(p.name()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}