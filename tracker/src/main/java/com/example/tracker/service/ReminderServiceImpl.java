package com.example.tracker.service;

import com.example.tracker.dto.ReminderDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.MailSendFailedException;
import com.example.tracker.mapper.ReminderMapper;
import com.example.tracker.model.Reminder;
import com.example.tracker.model.ReminderType;
import com.example.tracker.model.User;
import com.example.tracker.repository.ReminderRepository;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.ReminderService;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.utils.HtmlPdfGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final ReminderMapper reminderMapper;
    private final MailService mailService;
    private final TransactionService transactionService;

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
        User user =  this.userRepository.findById(reminderDTO.getUserId()).orElseThrow(() -> new ElementNotFoundException("User with given id not found!"));
        if (reminderDTO.getNextRun() == null){
            reminderDTO.setNextRun(LocalDate.now());
        }
        Reminder savedReminder = this.reminderRepository.save(this.reminderMapper.fromReminderDTO(reminderDTO, user));
        return this.reminderMapper.toReminderDTO(savedReminder);
    }

    @Override
    public ReminderDTO update(ReminderDTO reminderDTO) throws ElementNotFoundException {
        User user =  this.userRepository.findById(reminderDTO.getUserId()).orElseThrow(() -> new ElementNotFoundException("User with given id not found!"));
        Reminder reminder = this.reminderMapper.fromReminderDTO(reminderDTO, user);
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
    public void sendReminders(List<Reminder> reminders) {
        HtmlPdfGenerator htmlPdfGenerator = new HtmlPdfGenerator();
        for (Reminder reminder: reminders){
            Map<String, Object> templateData = new HashMap<>();
            if (reminder.getType().equals(ReminderType.Total)){
                LocalDate startDate = reminder.getNextRun().minusDays(reminder.getRepeatRate());
                Double amount = this.transactionService.getTotalSpentForUserInTimePeriod(reminder.getUser().getUserId(), startDate, reminder.getNextRun());
                templateData.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));
                templateData.put("endDate", reminder.getNextRun().format(DateTimeFormatter.ISO_DATE));
                templateData.put("spent", amount);
                templateData.put("user", reminder.getUser().getEmail());
                String htmlData = htmlPdfGenerator.parseReportTemplate(templateData, "reminder-total");
                try {
                    this.mailService.sendHtmlEmail(reminder.getUser().getEmail(), htmlData, "Expense tracker reminder");
                } catch (MessagingException e) {
                    throw new MailSendFailedException("Failed sending email for reminder!");
                }
            }
        }

    }

    @Override
    public void updateReminders(List<Reminder> reminders) {

    }


}
