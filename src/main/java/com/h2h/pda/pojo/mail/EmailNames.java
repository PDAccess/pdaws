package com.h2h.pda.pojo.mail;

public enum EmailNames {
    DEFAULT_MAIL("defaultMail"),
    MFA_MAIL("mfaMail"),
    NEW_USER("newUserMail"),
    CHANGE_EMAIL("changeMail"),
    PASSWORD_RESET("passwordResetMail"),
    APPROVE_PASSWORD_CHANGE("approvedPasswordResetMail"),
    EX_TRACE_ACTION("exTraceAlarmMail"),
    EX_AUTH_ACTION("exAuthAlarmMail"),
    EX_FILE_ACTION("exFileAlarmMail");

    private final String mailName;

    EmailNames(String mailName) {
        this.mailName = mailName;
    }

    public String getMailName() {
        return mailName;
    }
}
