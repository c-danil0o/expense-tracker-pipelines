package com.example.tracker.service;

import com.example.tracker.exceptions.MailSendFailedException;
import com.example.tracker.utils.BudgetCapExceed;
import com.example.tracker.utils.EmailReminder;
import com.example.tracker.utils.HtmlPdfGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public void sendReminders(List<EmailReminder> reminders) {
        HtmlPdfGenerator htmlPdfGenerator = new HtmlPdfGenerator();
        for (EmailReminder reminder : reminders) {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("startDate", reminder.getStartDate().format(DateTimeFormatter.ISO_DATE));
            templateData.put("endDate", reminder.getEndDate().format(DateTimeFormatter.ISO_DATE));
            templateData.put("spent", reminder.getSpent());
            templateData.put("user", reminder.getUser());
            String htmlData = htmlPdfGenerator.parseReportTemplate(templateData, "reminder-total");
            try {
                this.sendHtmlEmail(reminder.getUser(), htmlData, "Expense tracker reminder");
                System.out.println("Sent email to " + reminder.getUser());
            } catch (MessagingException e) {
                throw new MailSendFailedException("Failed sending email for reminder!");
            }
        }
    }
    public void sendBudgetCapReminder(BudgetCapExceed budgetCapExceed){
        HtmlPdfGenerator htmlPdfGenerator = new HtmlPdfGenerator();
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("user", budgetCapExceed.getUserEmail());
        templateData.put("category", budgetCapExceed.getCategory());
        templateData.put("spent", budgetCapExceed.getAmount());
        templateData.put("budgetCap", budgetCapExceed.getBudgetCap());

        String htmlData = htmlPdfGenerator.parseReportTemplate(templateData, "reminder-budget-cap");
        try {
            this.sendHtmlEmail(budgetCapExceed.getUserEmail(), htmlData, "Expense tracker reminder");
        } catch (MessagingException e) {
            throw new MailSendFailedException("Failed sending email for reminder!");
        }


    }


}
