package com.example.tracker.service.interfaces;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.exceptions.TransactionGroupAlreadyExistsException;
import com.example.tracker.exceptions.TransactionGroupNotFoundException;
import com.example.tracker.model.Reminder;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.utils.BudgetCapExceed;
import com.example.tracker.utils.EmailReminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService extends CrudService<TransactionDTO, Long> {
    TransactionGroupDTO createGroup(TransactionGroupDTO transactionGroup) throws TransactionGroupAlreadyExistsException;
    TransactionGroupDTO getGroupById(Long id) throws TransactionGroupNotFoundException;
    List<TransactionDTO> query(LocalDateTime startDate, LocalDateTime endDate, String type, String currency,
                                      String category, String status, Integer page, Integer pageSize, String sortParam);
    Double getTotalSpentForUserInTimePeriod(Long userId, LocalDate startDate, LocalDate endDate);
    List<EmailReminder> generateReminders(List<Reminder> reminders);

}
