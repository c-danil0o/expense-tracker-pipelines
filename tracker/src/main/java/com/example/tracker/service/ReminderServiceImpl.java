package com.example.tracker.service;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.mapper.ReminderMapper;
import com.example.tracker.model.Reminder;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.model.User;
import com.example.tracker.repository.ReminderRepository;
import com.example.tracker.service.interfaces.ReminderService;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.service.interfaces.UserService;
import com.example.tracker.utils.BudgetCapExceed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final ReminderMapper reminderMapper;
    private final MailService mailService;

    @Override
    public List<ReminderDTO> findAll() {
        return this.reminderRepository.findAll().stream().map(this.reminderMapper::toReminderDTO).toList();
    }

    @Override
    public ReminderDTO findById(Long id) throws ElementNotFoundException {
        return this.reminderMapper.toReminderDTO(this.reminderRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("Reminder with given id not found!")));
    }

    @Override
    public ReminderDTO save(ReminderDTO reminderDTO) {
        User user =  this.userService.findEntityById(reminderDTO.getUserId());
        if (reminderDTO.getNextRun() == null){
            reminderDTO.setNextRun(LocalDate.now().plusDays(reminderDTO.getDaysSpan()));
        }
        TransactionGroup group = this.transactionService.getGroupById(reminderDTO.getTransactionGroupId());
        Reminder savedReminder = this.reminderRepository.save(this.reminderMapper.fromReminderDTO(reminderDTO, user, group));
        return this.reminderMapper.toReminderDTO(savedReminder);
    }

    @Override
    public ReminderDTO update(ReminderDTO reminderDTO) throws ElementNotFoundException {
        User user =  this.userService.findEntityById(reminderDTO.getUserId());
        TransactionGroup group = this.transactionService.getGroupById(reminderDTO.getTransactionGroupId());
        Reminder reminder = this.reminderMapper.fromReminderDTO(reminderDTO, user, group);
        reminder.setId(reminderDTO.getId());

        Reminder savedReminder = this.reminderRepository.save(reminder);
        return this.reminderMapper.toReminderDTO(savedReminder);
    }

    @Override
    public void delete(Long id) throws ElementNotFoundException {
        if (this.reminderRepository.existsById(id)){
            this.reminderRepository.deleteById(id);
        }else{
            throw new ElementNotFoundException("Reminder with given id not found!");
        }

    }
    @Override
    public List<Reminder> getRemindersForToday() {
        return this.reminderRepository.getRemindersForDate(LocalDate.now());
    }

    @Override
    public void updateReminders(List<Reminder> reminders) {
        for (Reminder reminder: reminders){
            reminder.setNextRun(LocalDate.now().plusDays(reminder.getDaysSpan()));
            this.reminderRepository.save(reminder);
        }

    }
    @Override
    public Reminder findReminderByUserIdAndGroupId(Long userId, Long transactionGroupId) {
        return this.reminderRepository.findReminderByUserIdAndTransactionGroupIdBudgetCap(userId, transactionGroupId).orElse(null);
    }

    @Override
    public void sendNotificationIfBudgetCapExceeded(Long userId, Long transactionGroupId) {
        Reminder reminder = this.findReminderByUserIdAndGroupId(userId, transactionGroupId);
        if (reminder != null) {
            double budgetCap = reminder.getGroup().getBudgetCap();
            double spentAmount = this.transactionService.getTotalSpentForUserInTimePeriodForTransactionGroup(userId,
                    reminder.getNextRun().minusDays(reminder.getDaysSpan()), reminder.getNextRun(), transactionGroupId);
            if (budgetCap <= spentAmount)
                this.mailService.sendBudgetCapReminder(new BudgetCapExceed(reminder.getUser().getEmail(), budgetCap, spentAmount, reminder.getGroup().getName()));
        }

    }

}
