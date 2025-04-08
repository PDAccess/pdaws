package com.h2h.pda.jwt;

import java.util.Arrays;
import java.util.Optional;

public enum LoginTypes {
    STANDARD, LDAP, INTERNAL;

    public static LoginTypes parseLogin(String loginType) {
        Optional<LoginTypes> first = Arrays.stream(LoginTypes.values())
                .filter(lt -> lt.name().equalsIgnoreCase(loginType)).findFirst();
        return first.orElse(STANDARD);
    }
}
