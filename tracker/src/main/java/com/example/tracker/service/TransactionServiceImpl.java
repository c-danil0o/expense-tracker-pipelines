package com.example.tracker.service;

import com.eclipsesource.json.JsonObject;
import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.InvalidTransactionGroupException;
import com.example.tracker.exceptions.TransactionGroupAlreadyExistsException;
import com.example.tracker.exceptions.TransactionGroupNotFoundException;
import com.example.tracker.filter.TransactionSpecification;
import com.example.tracker.mapper.TransactionMapper;
import com.example.tracker.model.*;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import com.example.tracker.service.interfaces.EventStreamService;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.service.interfaces.UserService;
import com.example.tracker.utils.EmailReminder;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final TransactionMapper transactionMapper;
    private final UserService userService;
    private final EventStreamService eventStreamService;


    @Transactional
    @Override
    public TransactionGroupDTO createGroup(TransactionGroupDTO transactionGroupDTO) throws TransactionGroupAlreadyExistsException {
        if (transactionGroupDTO.getUserId() == null) {
            if (this.transactionGroupRepository.getByName(transactionGroupDTO.getName()) != null) {
                throw new TransactionGroupAlreadyExistsException("Group with given name already exists!");
            }
        } else {
            if (this.transactionGroupRepository.getByUserId(transactionGroupDTO.getUserId()).stream().anyMatch(group1 -> group1.getName().equalsIgnoreCase(transactionGroupDTO.getName()))) {
                throw new TransactionGroupAlreadyExistsException("Group with given name already exists!");
            }
        }
        if (transactionGroupDTO.getBudgetCap() != null && transactionGroupDTO.getUserId() == null)
            throw new InvalidTransactionGroupException("Transaction group must contain user id when budgetCap is defined!");

        TransactionGroup savedTransactionGroup = this.transactionGroupRepository.save(this.transactionMapper.fromTransactionGroupDTO(transactionGroupDTO));
        String budgetCap = "";
        this.eventStreamService.sendRecord(LocalDateTime.now(), "Transaction_Group_CREATED", "transaction", transactionGroupDTO.getName() + "-" + transactionGroupDTO.getBudgetCapSafe(), "BASIC");
        return this.transactionMapper.toTransactionGroupDTO(savedTransactionGroup);
    }


    @Override
    public TransactionGroup getGroupById(Long id) throws TransactionGroupNotFoundException {
        return this.transactionGroupRepository.findById(id).orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!"));
    }

    @Override
    public List<TransactionDTO> findAll() {
        return this.transactionRepository.findAll().stream().map(this.transactionMapper::toTransactionDTO).toList();
    }

    private String formatTransactionQuery(LocalDateTime startDate, LocalDateTime endDate, String type, String currency,
                                          String category, String status){
        JsonObject json = new JsonObject();
        if (startDate != null)
            json.add("startDate", startDate.toString());
        if (endDate != null)
            json.add("endDate", endDate.toString());
        if (type != null)
            json.add("type", type);
        if (currency != null)
            json.add("currency", currency);
        if (category != null)
            json.add("category", category);
        if (status != null)
            json.add("status", status);
        return json.toString();
    }

    private String getTransactionMetadata(Transaction transaction){
        JsonObject json = new JsonObject();
        json.add("type", transaction.getType().toString());
        json.add("currency", transaction.getCurrency());
        json.add("amount", transaction.getAmount());
        json.add("status", transaction.getStatus().toString());
        json.add("category", transaction.getTransactionGroup().getName());
        return json.toString();
    }

    @Transactional
    @Override
    public List<TransactionDTO> query(LocalDateTime startDate, LocalDateTime endDate, String type, String currency,
                                      String category, String status, Integer page, Integer pageSize, String sortParam) {
        Specification<Transaction> filters = Specification.where(startDate == null && endDate == null ? null :
                        TransactionSpecification.isBetweenDates(startDate, endDate)).
                and(StringUtils.isBlank(type) ? null : TransactionSpecification.hasTransactionType(type))
                .and(StringUtils.isBlank(currency) ? null : TransactionSpecification.hasCurrency(currency))
                .and(StringUtils.isBlank(category) ? null : TransactionSpecification.isCategory(category))
                .and(StringUtils.isBlank(status) ? null : TransactionSpecification.hasTransactionStatus(status));
        Pageable pageable = null;
        if (page != null && pageSize != null) {
            if (!StringUtils.isBlank(sortParam))
                pageable = PageRequest.of(page, pageSize, Sort.by(sortParam));
            else
                pageable = PageRequest.of(page, pageSize);
        } else if (!StringUtils.isBlank(sortParam)) {
            pageable = Pageable.unpaged(Sort.by(sortParam));
        } else {
            pageable = Pageable.unpaged();
        }
        List<TransactionDTO> result =this.transactionRepository.findAll(filters, pageable).stream().map(this.transactionMapper::toTransactionDTO).toList();
        this.eventStreamService.sendRecord(LocalDateTime.now(), "Transaction_Query_EXECUTED", "transaction",
                this.formatTransactionQuery(startDate, endDate, type, currency, category, status), "BASIC");
        return result;

    }

    @Override
    public double getTotalSpentForUserInTimePeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        Double amount = this.transactionRepository.getTotalSpentForUserInTimePeriod(userId, startDate.atTime(0,0), endDate.atTime(0,0));
        if (amount == null)
            return 0.0;
        return amount;
    }

    @Override
    public TransactionDTO findById(Long transactionId) throws ElementNotFoundException {
        return this.transactionMapper.toTransactionDTO(this.transactionRepository.findById(transactionId).orElseThrow(() -> new ElementNotFoundException("No such element with given id!")));
    }

    @Transactional
    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        User user = this.userService.findEntityById(transactionDTO.getUserId());
        TransactionGroup transactionGroup = this.transactionGroupRepository.findById(transactionDTO.getTransactionGroupId())
                .orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!"));
        Transaction savedTransaction = this.transactionRepository.save(this.transactionMapper.fromTransactionDTO(transactionDTO, user, transactionGroup));
        this.eventStreamService.sendRecord(LocalDateTime.now(), "Transaction_CREATED", "transaction", this.getTransactionMetadata(savedTransaction), "BASIC");
        return this.transactionMapper.toTransactionDTO(savedTransaction);
    }

    @Transactional
    @Override
    public TransactionDTO update(TransactionDTO newTransaction) throws ElementNotFoundException {
        User user = this.userService.findEntityById(newTransaction.getUserId());
        TransactionGroup transactionGroup = this.transactionGroupRepository.findById(newTransaction.getTransactionGroupId())
                .orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!"));
        if (!this.transactionRepository.existsById(newTransaction.getId()))
            throw new ElementNotFoundException("Transaction with given id doesn't exist!");
        Transaction transaction = this.transactionMapper.fromTransactionDTO(newTransaction, user, transactionGroup);
        transaction.setId(newTransaction.getId());
        Transaction savedTransaction = this.transactionRepository.save(transaction);
        this.eventStreamService.sendRecord(LocalDateTime.now(), "Transaction_UPDATED", "transaction", this.getTransactionMetadata(savedTransaction),"BASIC");
        return this.transactionMapper.toTransactionDTO(savedTransaction);
    }

    @Override
    public void delete(Long transactionId) throws ElementNotFoundException {
        if (this.transactionRepository.existsById(transactionId)) {
            Transaction transaction = this.transactionRepository.findById(transactionId).orElse(null);
            this.transactionRepository.deleteById(transactionId);
            this.eventStreamService.sendRecord(LocalDateTime.now(), "Transaction_DELETED", "transaction", this.getTransactionMetadata(transaction), "BASIC");
        } else {
            throw new ElementNotFoundException("No such element with given ID!");
        }
    }

    @Override
    public List<EmailReminder> generateReminders(List<Reminder> reminders) {
        List<EmailReminder> emailReminders = new ArrayList<>();
        for (Reminder reminder : reminders) {
            if (reminder.getType().equals(ReminderType.Total)) {
                LocalDate startDate = reminder.getNextRun().minusDays(reminder.getDaysSpan());
                double amount = this.getTotalSpentForUserInTimePeriod(reminder.getUser().getUserId(), startDate, reminder.getNextRun());
                emailReminders.add(new EmailReminder(startDate, reminder.getNextRun(), reminder.getUser().getEmail(), amount));
            }
        }
        return emailReminders;
    }
    @Override
    public double getTotalSpentForUserInTimePeriodForTransactionGroup(Long userId, LocalDate startDate, LocalDate endDate, Long transactionGroupId){
        Double amount = this.transactionRepository.getTotalSpentForUserInTimePeriodForTransactionGroup
                (userId, startDate.atTime(0,0), endDate.atTime(0,0), transactionGroupId);
        if (amount == null)
            return 0.0;
        return amount;
    }


}
