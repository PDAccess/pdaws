package com.h2h.pda.pojo.metric;

public enum Gauges {
    @Deprecated
    NEW_PARTITION_CREATE("pdaws_new_partition_create"),
    @Deprecated
    NEW_PARTITION_DROP("pdaws_new_partition_create");

    protected final String name;

    Gauges(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
