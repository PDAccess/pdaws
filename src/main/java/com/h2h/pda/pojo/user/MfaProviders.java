package com.h2h.pda.pojo.user;

import java.util.stream.Stream;

public enum MfaProviders {
    GOOGLE_AUTHENTICATOR("google"),
    SMS("sms"),
    EMAIL("email");

    private String type;

    MfaProviders(String type) {
        this.type = type;
    }

    public static MfaProviders of(String type) {
        return Stream.of(MfaProviders.values())
                .filter(p -> p.type.equals(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
