package com.example.tracker.utils;

import com.example.tracker.model.Reminder;
import com.example.tracker.service.MailService;
import com.example.tracker.service.interfaces.ReminderService;
import com.example.tracker.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RequiredArgsConstructor
@Component
public class NotificationScheduler {
    private ReminderService reminderService;
    private MailService mailService;
    private TransactionService transactionService;


    @Scheduled(cron = "0 12 * * * *")
    public void checkDailyForScheduledNotifications(){
        List<Reminder> reminders =  this.reminderService.getRemindersForToday();
        this.mailService.sendReminders(this.transactionService.generateReminders(reminders));
        this.reminderService.updateReminders(reminders);
    }

}
