package com.h2h.pda.pojo.policy;

import java.util.Optional;
import java.util.stream.Stream;

public enum PolicyBehavior {
    BLACK("B"), WHITE("W");

    String property;

    PolicyBehavior(String property) {
        this.property = property;
    }

    public static Optional<PolicyBehavior> of(String property) {
        return Stream.of(PolicyBehavior.values())
                .filter(p -> p.property.equals(property))
                .findFirst();
    }

    public static PolicyBehavior ofElseThrow(String property) {
        return Stream.of(PolicyBehavior.values())
                .filter(p -> p.property.equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getPropertyString() {
        return property;
    }
}
