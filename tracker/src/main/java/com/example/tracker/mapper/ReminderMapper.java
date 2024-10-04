package com.example.tracker.mapper;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.model.Reminder;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderMapper {
    public ReminderDTO toReminderDTO(Reminder reminder){
        return ReminderDTO.builder().id(reminder.getId()).type(reminder.getType()).userId(reminder.getUser().getUserId()).
                nextRun(reminder.getNextRun()).daysSpan(reminder.getDaysSpan()).transactionGroupId(reminder.getGroup().getId()).build();
    }
    public Reminder fromReminderDTO(ReminderDTO reminderDTO, User user, TransactionGroup group){
        return Reminder.builder().id(reminderDTO.getId()).type(reminderDTO.getType()).nextRun(reminderDTO.getNextRun())
                .daysSpan(reminderDTO.getDaysSpan()).user(user).group(group).build();
    }
}
