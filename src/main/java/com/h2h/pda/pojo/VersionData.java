package com.h2h.pda.pojo;

public class VersionData {
    private String version;
    private String tag;

    public String getVersion() {
        return version;
    }

    public VersionData setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public VersionData setTag(String tag) {
        this.tag = tag;
        return this;
    }
}
