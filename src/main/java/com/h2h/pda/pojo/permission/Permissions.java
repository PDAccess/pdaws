package com.h2h.pda.pojo.permission;

import java.util.stream.Stream;

public enum Permissions {
    CAN_CONNECT(1), CAN_SEE_PASSWORD(2), CAN_CHANGE_PASSWORD(3),
    CAN_AUTHORIZE_OTHER_USERS(4), CAN_JOIN_SESSIONS(5),
    CAN_CONNECTION_WITH_REQUEST_PERMISSION(6), CAN_MANAGE_OTHER_USERS(7);

    Integer property;

    Permissions(Integer property) {
        this.property = property;
    }

    public static Permissions of(Integer property) {
        return Stream.of(Permissions.values())
                .filter(p -> p.property.equals(property))
                .findFirst()
                .orElseThrow(() -> makeException(property));
    }

    private static IllegalArgumentException makeException(Integer property) {
        return new IllegalArgumentException(String.format("There is no parameter for: %s", property));
    }

    public Integer getProperty() {
        return property;
    }
}
