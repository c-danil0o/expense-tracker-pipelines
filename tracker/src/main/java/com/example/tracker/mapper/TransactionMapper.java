package com.example.tracker.mapper;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.TransactionGroupNotFoundException;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.model.User;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    public TransactionGroupDTO toTransactionGroupDTO(TransactionGroup transactionGroup) {
        return TransactionGroupDTO.builder().id(transactionGroup.getId()).name(transactionGroup.getName()).
                budgetCap(transactionGroup.getBudgetCap()).userId(transactionGroup.getUserId()).build();
    }

    public TransactionGroup fromTransactionGroupDTO(TransactionGroupDTO transactionGroupDTO) {
        return TransactionGroup.builder().id(transactionGroupDTO.getId()).budgetCap(transactionGroupDTO.getBudgetCap()).
                name(transactionGroupDTO.getName()).userId(transactionGroupDTO.getUserId()).build();
    }

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        return TransactionDTO.builder().id(transaction.getId()).userId(transaction.getUser().getUserId()).
                timestamp(transaction.getTimestamp()).type(transaction.getType()).currency(transaction.getCurrency())
                .amount(transaction.getAmount()).status(transaction.getStatus()).repeatType(transaction.getRepeatType())
                .transactionGroupId(transaction.getTransactionGroup().getId()).build();
    }

    public Transaction fromTransactionDTO(TransactionDTO transactionDTO, User user, TransactionGroup transactionGroup) {
        return Transaction.builder().id(transactionDTO.getId()).user(user).timestamp(transactionDTO.getTimestamp()).
                type(transactionDTO.getType()).currency(transactionDTO.getCurrency()).amount(transactionDTO.getAmount())
                .repeatType(transactionDTO.getRepeatType()).transactionGroup(transactionGroup).
                status(transactionDTO.getStatus()).build();
    }


}
