package com.example.tracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.address.report}")
    private String senderEmailAddress;

    public void sendHtmlEmail(String destinationAddress, String htmlContent, String subject) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.setFrom(new InternetAddress(senderEmailAddress));
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, destinationAddress);
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");

        javaMailSender.send(mimeMessage);
    }
}
