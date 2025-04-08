package com.h2h.pda.pojo.metric;

public enum Counters {
    VERSION("pdaws_commit_id"),
    EMAIL_COUNTS("pdaws_email_counts"),
    @Deprecated
    COMMAND_COUNTS("pdaws_command_counts"),
    @Deprecated
    ALARM_COUNTS("pdaws_alarm_counts"),
    PASSWORD_JOB_COUNTS("pdaws_" +
            ""),
    LOGIN_ATTEMPT_COUNT("pdaws_login_attempt"),
    LOGIN_SUCCESS_COUNT("pdaws_login_success"),
    LOGIN_FAIL_COUNT("pdaws_login_fail"),
    SYSTEM_STATUS_COUNT("pdaws_system_status");

    protected final String name;

    Counters(String name) {
        this.name = name;
    }

    public String metricName() {
        return name;
    }
}
