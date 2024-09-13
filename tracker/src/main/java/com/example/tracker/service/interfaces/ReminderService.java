package com.example.tracker.service.interfaces;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.model.Reminder;

import java.util.List;

public interface ReminderService extends CrudService<ReminderDTO, Long> {
    List<Reminder> getRemindersForToday();

    void updateReminders(List<Reminder> reminders);

    Reminder findReminderByUserIdAndGroupId(Long userId, Long transactionGroupId);

    void sendNotificationIfBudgetCapExceeded(Long userId, Long transactionGroupId);
}
