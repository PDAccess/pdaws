package com.h2h.pda.pojo;

public class SettingParam {
    private String tag;
    private String value;

    public SettingParam() {
    }

    public SettingParam(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
