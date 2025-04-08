package com.h2h.pda.config;

public enum AsyncQueues {
    ACTION_QUEUE(QueueNames.ACTION_QUEUE),
    CREDENTIAL_MANAGER_QUEUE(QueueNames.CREDENTIAL_MANAGER_QUEUE),
    SEND_SMS_QUEUE(QueueNames.SEND_SMS_QUEUE),
    SEND_EMAIL_QUEUE(QueueNames.SEND_EMAIL_QUEUE),
    ACTION_PDA_QUEUE(QueueNames.ACTION_PDA_QUEUE);

    public interface QueueNames {
        String ACTION_QUEUE = "action.alarm.queue";
        String CREDENTIAL_MANAGER_QUEUE = "credential.manager.queue";
        String SEND_SMS_QUEUE = "pdaccess.send.sms";
        String SEND_EMAIL_QUEUE = "pdaccess.send.email";
        String ACTION_PDA_QUEUE = "pdaccess.action.pda";
    }

    private final String queueName;

    AsyncQueues(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
