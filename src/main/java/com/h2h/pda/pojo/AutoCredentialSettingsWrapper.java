package com.h2h.pda.pojo;

public class AutoCredentialSettingsWrapper {

    private boolean enabled;
    private Integer autoCredentialTime;
    private String autoCredentialTimeType;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getAutoCredentialTime() {
        return autoCredentialTime;
    }

    public void setAutoCredentialTime(Integer autoCredentialTime) {
        this.autoCredentialTime = autoCredentialTime;
    }

    public String getAutoCredentialTimeType() {
        return autoCredentialTimeType;
    }

    public void setAutoCredentialTimeType(String autoCredentialTimeType) {
        this.autoCredentialTimeType = autoCredentialTimeType;
    }

}
