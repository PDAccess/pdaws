package com.h2h.pda.pojo.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum ServiceType {
    MYSQL(1, "mysql", ServiceMeta.DATABASE),
    POSTGRESQL(2, "postgresql", ServiceMeta.DATABASE),
    MSSQL(3, "msserver", ServiceMeta.DATABASE),
    ORACLE(4, "oracle", ServiceMeta.DATABASE),
    SSH(5, "ssh", ServiceMeta.TERMINAL),
    TELNET(6, "telnet", ServiceMeta.TERMINAL),
    RDP(7, "rdp", ServiceMeta.TERMINAL),
    VNC(8, "vnc", ServiceMeta.TERMINAL),
    MONGODB(9, "mongodb", ServiceMeta.DATABASE),
    WEBAPP(10, "webapp", ServiceMeta.WEBAPP),
    LDAP(11, "ldap", ServiceMeta.LDAP),
    UNKNOWN(100, "unknow", null);

    int i;
    String name;
    ServiceMeta meta;

    ServiceType(int i, String name, ServiceMeta meta) {
        this.i = i;
        this.name = name;
        this.meta = meta;
    }

    public static ServiceType of(Integer id) {
        return Stream.of(ServiceType.values())
                .filter(p -> p.i == id)
                .findFirst()
                .orElse(UNKNOWN);
    }

    public String getTypeName() {
        return this.name;
    }

    @JsonCreator
    public static ServiceType forValue(Integer value) {
        return of(value);
    }

    @JsonValue
    public Integer getIntValue() {
        return i;
    }

    public ServiceMeta getMeta() {
        return meta;
    }
}