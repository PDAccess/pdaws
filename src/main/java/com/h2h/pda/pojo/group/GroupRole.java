package com.h2h.pda.pojo.group;

import java.util.Objects;
import java.util.stream.Stream;

public enum GroupRole {
    USER, ADMIN;

    public static GroupRole of(String id) {
        Objects.requireNonNull(id);

        return Stream.of(GroupRole.values())
                .filter(p -> id.equalsIgnoreCase(p.name()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
