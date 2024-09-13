package com.example.tracker.utils;

import com.example.tracker.model.Reminder;
import com.example.tracker.service.MailService;
import com.example.tracker.service.interfaces.ReminderService;
import com.example.tracker.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class NotificationScheduler {
    private final ReminderService reminderService;
    private final MailService mailService;
    private final TransactionService transactionService;


    // every 3 minutes for testing
    @Scheduled(cron = "*/3 * * * * *")
    public void checkDailyForScheduledNotifications(){
        List<Reminder> reminders =  this.reminderService.getRemindersForToday();
        this.mailService.sendReminders(this.transactionService.generateReminders(reminders));
        this.reminderService.updateReminders(reminders);
    }

}
