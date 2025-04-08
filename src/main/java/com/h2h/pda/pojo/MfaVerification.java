package com.h2h.pda.pojo;

public class MfaVerification {

    private Boolean googleAuthenticator;
    private Boolean sms;
    private Boolean email;

    public MfaVerification() {
        googleAuthenticator = false;
        sms = false;
        email = false;
    }

    public Boolean getGoogleAuthenticator() {
        return googleAuthenticator;
    }

    public void setGoogleAuthenticator(Boolean googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    public Boolean getSms() {
        return sms;
    }

    public void setSms(Boolean sms) {
        this.sms = sms;
    }

    public Boolean getEmail() {
        return email;
    }

    public void setEmail(Boolean email) {
        this.email = email;
    }
}
