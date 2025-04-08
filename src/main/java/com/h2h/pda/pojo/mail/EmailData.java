package com.h2h.pda.pojo.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class EmailData {
    private EmailPropsData emailPropsData;
    private String toMail;
    private String subject;
    private String text;
    private String description;
    private String link;

    private EmailNames mailName;
    private Map<String, Object> html = new HashMap<>();

    public EmailData() {
        // Constructor
    }

    public EmailNames getMailName() {
        return mailName;
    }

    public void setMailName(EmailNames mailName) {
        this.mailName = mailName;
    }

    public EmailPropsData getEmailPropsData() {
        return emailPropsData;
    }

    public void setEmailPropsData(EmailPropsData emailPropsData) {
        this.emailPropsData = emailPropsData;
    }

    public String getToMail() {
        return toMail;
    }

    public void setToMail(String toMail) {
        this.toMail = toMail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getHtml() {
        return html;
    }

    public void setHtml(Map<String, Object> html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EmailData.class.getSimpleName() + "[", "]")
                .add("emailPropsData=" + emailPropsData)
                .add("toMail='" + toMail + "'")
                .add("subject='" + subject + "'")
                .add("text='" + text + "'")
                .add("mailName='" + mailName + "'")
                .add("html=" + html)
                .toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
