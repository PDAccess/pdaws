package com.h2h.pda.entity;

import javax.persistence.*;

@Entity
@Table(name = "system_settings")
public class SettingsEntity extends BaseEntity {

    @Id
    private Integer id;
    private String settingTag;
    private String settingValue;
    private String settingShortCode;
    private String settingCategory;

    @Deprecated
    private String hostname;
    @Deprecated
    private String port;

    public SettingsEntity() {
        // Constructor
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSettingTag() {
        return settingTag;
    }

    public void setSettingTag(String settingTag) {
        this.settingTag = settingTag;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getSettingShortCode() {
        return settingShortCode;
    }

    public void setSettingShortCode(String settingShortCode) {
        this.settingShortCode = settingShortCode;
    }

    public String getSettingCategory() {
        return settingCategory;
    }

    public void setSettingCategory(String settingCategory) {
        this.settingCategory = settingCategory;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
