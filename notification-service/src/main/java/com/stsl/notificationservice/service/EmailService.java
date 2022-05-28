package com.stsl.notificationservice.service;


import com.stsl.notificationservice.configuration.AppConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {

    private final AppConfiguration appConfiguration;

    public EmailService(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(appConfiguration.getMailtrapConfig().getHost());
        mailSenderImpl.setPort(appConfiguration.getMailtrapConfig().getPort());
        mailSenderImpl.setUsername(appConfiguration.getMailtrapConfig().getUsername());
        mailSenderImpl.setPassword(appConfiguration.getMailtrapConfig().getPassword());
        mailSenderImpl.setProtocol(appConfiguration.getMailtrapConfig().getProtocol());
        return mailSenderImpl;
    }

    public void sendEmail(String email, String userName, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom(appConfiguration.getMTrapConfig().getFrom());
        try {
            getJavaMailSender().send(mailMessage);
        } catch (MailException ex) {
            log.info("email error {}", ex);
        }

    }
//    @Deemene@Saint#Epleele20
}
