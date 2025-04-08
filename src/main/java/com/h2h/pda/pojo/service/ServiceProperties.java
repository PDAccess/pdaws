package com.h2h.pda.pojo.service;

import java.util.Optional;
import java.util.stream.Stream;

public enum ServiceProperties {
    DB_NAME("db_name"),
    PATH("path"),
    VIDEO_RECORD("video_record"),
    REMOTE_APP("remote-app"),
    REMOTE_APP_ARGS("remote-app-args"),
    REMOTE_APP_DIR("remote-app-dir"),
    MAXIMUM_SESSION("maximum-session"),
    IDLE_TIMEOUT("idle-timeout"),
    USE_DEFAULT_CONNECTION_USER("use-default-connection-user"),
    LDAP_URL("ldap-url"),
    LDAP_BASE_DN("ldap-base-dn"),
    LDAP_BIND_DN("ldap-bind-dn"),
    LDAP_BIND_PASSWORD("ldap-bind-password"),
    LDAP_START_TLS("ldap-start-tls"),
    LDAP_INSECURE_TLS("ldap-insecure-tls");

    String property;

    ServiceProperties(String property) {
        this.property = property;
    }

    public static Optional<ServiceProperties> of(String property) {
        return Stream.of(ServiceProperties.values())
                .filter(p -> p.property.equals(property))
                .findFirst();
    }

    public static ServiceProperties ofElseThrow(String property) {
        return Stream.of(ServiceProperties.values())
                .filter(p -> p.property.equals(property))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getPropertyString() {
        return property;
    }
}
