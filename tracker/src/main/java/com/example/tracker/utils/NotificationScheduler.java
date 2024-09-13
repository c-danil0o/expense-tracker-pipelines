package com.example.tracker.utils;

import com.example.tracker.model.Reminder;
import com.example.tracker.service.interfaces.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduler {
    @Autowired
    private ReminderService reminderService;

    @Scheduled(cron = "0 12 * * * *")
    public void checkDailyForScheduledNotifications(){
        List<Reminder> reminders =  this.reminderService.getRemindersForToday();
        this.reminderService.sendReminders(reminders);
        this.reminderService.updateReminders(reminders);
    }

}
