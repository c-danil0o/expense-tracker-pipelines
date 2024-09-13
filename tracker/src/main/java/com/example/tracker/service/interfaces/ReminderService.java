package com.example.tracker.service.interfaces;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.model.Reminder;

import java.util.List;

public interface ReminderService extends CrudService<ReminderDTO, Long> {
    List<Reminder> getRemindersForToday();
    
    void sendReminders(List<Reminder> reminders);
    void updateReminders(List<Reminder> reminders);
}
