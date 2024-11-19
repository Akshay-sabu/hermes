package com.hodos.hermes.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    // Send simple email
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Mail sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error while sending mail to: {}", to, e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    // Send HTML email
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML

            mailSender.send(message);
            log.info("HTML mail sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error while sending HTML mail to: {}", to, e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    // Send email with attachment
    public void sendEmailWithAttachment(String to, String subject, String body,
                                        String attachmentName, byte[] attachmentData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Add attachment
            ByteArrayResource resource = new ByteArrayResource(attachmentData);
            helper.addAttachment(attachmentName, resource);

            mailSender.send(message);
            log.info("Mail with attachment sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error while sending mail with attachment to: {}", to, e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }
}