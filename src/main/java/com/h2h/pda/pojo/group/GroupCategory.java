package com.h2h.pda.pojo.group;

import java.util.Optional;
import java.util.stream.Stream;

public enum GroupCategory {
    NORMAL, LDAP;

    public static Optional<GroupCategory> of(String property) {
        return Stream.of(GroupCategory.values())
                .filter(p -> p.name().equalsIgnoreCase(property))
                .findFirst();
    }
}
