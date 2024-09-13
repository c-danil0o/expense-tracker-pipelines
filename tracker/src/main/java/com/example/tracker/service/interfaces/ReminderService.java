package com.example.tracker.service.interfaces;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.model.Reminder;
import com.example.tracker.utils.BudgetCapExceed;

import java.util.List;
import java.util.Optional;

public interface ReminderService extends CrudService<ReminderDTO, Long> {
    List<Reminder> getRemindersForToday();

    void updateReminders(List<Reminder> reminders);

    Reminder findReminderByUserIdAndGroupId(Long userId, Long transactionGroupId);
}
