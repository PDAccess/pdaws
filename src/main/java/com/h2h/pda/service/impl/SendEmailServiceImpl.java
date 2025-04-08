package com.h2h.pda.service.impl;

import com.h2h.pda.pojo.mail.EmailData;
import com.h2h.pda.pojo.mail.EmailNames;
import com.h2h.pda.service.api.MetricService;
import com.h2h.pda.service.api.SendEmailService;
import com.h2h.pda.service.api.SystemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.jms.Session;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static com.h2h.pda.config.AsyncQueues.QueueNames.SEND_EMAIL_QUEUE;
import static com.h2h.pda.pojo.metric.Counters.EMAIL_COUNTS;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    //public static final String EMAIL_QUEUE = "/queue/email_queue";

    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_USER = "mail.smtp.user";
    private static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_AUTH_MECHANISMS = "mail.smtp.auth.mechanisms";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private final Logger log = LoggerFactory.getLogger(SendEmailServiceImpl.class);

    @Autowired
    SystemSettings systemSettings;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MetricService metricService;

    @Autowired
    Environment env;

    @Autowired
    JmsTemplate jmsTemplate;

    AtomicInteger currentErrorCount = new AtomicInteger(0);


    @JmsListener(destination = SEND_EMAIL_QUEUE, containerFactory = "queueListenerFactory")
    public void sendEmail(@Payload EmailData data,
                          @Headers MessageHeaders headers,
                          Message message, Session session) {

        log.info("Email requested {}", data.toString());
        if (env.acceptsProfiles(Profiles.of("sonar", "dev")))
            return;

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = mailSender.getJavaMailProperties();

        if (data.getEmailPropsData() == null) {
            Optional<String> mail_host = systemSettings.tagValue("mail_host");
            if (!mail_host.isPresent()) {
                log.warn("There is no mail configuration. returning...");
                return;
            }
            props.put(MAIL_SMTP_HOST, mail_host.get());
            props.put(MAIL_SMTP_PORT, Integer.parseInt(systemSettings.tagValue("mail_port").get()));
            props.put(MAIL_SMTP_USER, systemSettings.tagValue("mail_address").get());
            String mailPassword = systemSettings.tagValue("mail_password").get();
            if (mailPassword != null)
                props.put(MAIL_SMTP_PASSWORD, mailPassword);

            props.put(MAIL_TRANSPORT_PROTOCOL, systemSettings.tagValue("mail_smtp").get());
            String mailAuth = systemSettings.tagValue("mail_auth").get();
            props.put(MAIL_SMTP_AUTH, mailAuth == null ? FALSE : TRUE);
            String mailAuthType = systemSettings.tagValue("mail_auth_type").get();
            if (mailAuthType != null)
                props.put(MAIL_SMTP_AUTH_MECHANISMS, mailAuthType);
            props.put(MAIL_SMTP_STARTTLS_ENABLE, systemSettings.tagValue("mail_starttls").get());
            props.put(MAIL_SMTP_SSL_ENABLE, systemSettings.tagValue("mail_ssl").get());
        } else {
            props.put(MAIL_SMTP_HOST, data.getEmailPropsData().getHost());
            props.put(MAIL_SMTP_PORT, data.getEmailPropsData().getPort());
            props.put(MAIL_SMTP_USER, data.getEmailPropsData().getEmail());
            if (data.getEmailPropsData().getPassword() != null)
                props.put(MAIL_SMTP_PASSWORD, data.getEmailPropsData().getPassword());

            props.put(MAIL_TRANSPORT_PROTOCOL, data.getEmailPropsData().getSmtp());
            props.put(MAIL_SMTP_AUTH, data.getEmailPropsData().getAuth() == null ? FALSE : data.getEmailPropsData().getAuth());
            if (data.getEmailPropsData().getAuthType() != null)
                props.put(MAIL_SMTP_AUTH_MECHANISMS, data.getEmailPropsData().getAuthType());
            props.put(MAIL_SMTP_STARTTLS_ENABLE, data.getEmailPropsData().getStarttls());
            props.put(MAIL_SMTP_SSL_ENABLE, data.getEmailPropsData().getSsl());
        }

        props.put("mail.debug", FALSE);
        if (TRUE.equals(props.get(MAIL_SMTP_AUTH))) {
            mailSender.setUsername(props.getProperty(MAIL_SMTP_USER));
            mailSender.setPassword(props.getProperty(MAIL_SMTP_PASSWORD));
        }

        try {
            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
                messageHelper.setFrom(props.getProperty(MAIL_SMTP_USER));
                messageHelper.setTo(data.getToMail().split(","));
                messageHelper.setSubject(data.getSubject());

                String host = systemSettings.tagValue("default_system_host_name").get();
                String mailLogo = systemSettings.tagValue("mail_logo").get();
                String companyName = systemSettings.tagValue("mail_company_name").get();
                String companyUrl = systemSettings.tagValue("mail_company_url").get();
                String companyAddress = systemSettings.tagValue("mail_company_address").get();

                if (host == null || host.equals(""))
                    host = "pda.h2hsecure.com";
                if (mailLogo == null || mailLogo.equals(""))
                    mailLogo = "data:image/png;base64,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";
                if (companyName == null || companyName.equals(""))
                    companyName = "H2HSecure Technologies, Inc";
                if (companyUrl == null || companyUrl.equals(""))
                    companyUrl = "https://www.h2hsecure.com/";
                if (companyAddress == null || companyAddress.equals(""))
                    companyAddress = "Tahtakale  Vasif Cinar Cd. No:23  34100  Fatih/Istanbul";

                Context context = new Context();
                context.setVariable("message", data.getText());
                context.setVariable("description", data.getDescription());
                context.setVariable("link", data.getLink());
                context.setVariable("host", host);
                context.setVariable("mailLogo", mailLogo);
                context.setVariable("companyName", companyName);
                context.setVariable("companyUrl", companyUrl);
                context.setVariable("information", "This e-mail has been send by PDAccess at " + companyName);
                context.setVariables(data.getHtml());

                mimeMessage.setContent(templateEngine.process(data.getMailName() == null
                        ? EmailNames.DEFAULT_MAIL.getMailName() : data.getMailName().getMailName(), context), "text/html");
            };

            mailSender.send(messagePreparator);

            metricService.getCounter(EMAIL_COUNTS).increment("Success Mail");

            if (currentErrorCount.get() > 0) {
                currentErrorCount.decrementAndGet();
            }
        } catch (Exception e) {
            log.error("Mail Send exception", e);
            metricService.getCounter(EMAIL_COUNTS).increment("Error Mail");
            int errorCount = currentErrorCount.get() > 7 ? currentErrorCount.get() : currentErrorCount.incrementAndGet();
            try {
                Thread.sleep(errorCount * errorCount * 1000l);
            } catch (InterruptedException ex) {
                // This can be avoided.
            }

        }
    }

    @Override
    public void pushEmailRequest(EmailData emailData) {
        jmsTemplate.convertAndSend(SEND_EMAIL_QUEUE, emailData);
    }
}