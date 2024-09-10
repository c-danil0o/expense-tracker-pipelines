package com.example.tracker.service;

import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final TransactionGroupRepository transactionGroupRepository;

    public TransactionService(TransactionRepository transactionRepository, TransactionGroupRepository transactionGroupRepository)
    {
        this.transactionRepository = transactionRepository;
        this.transactionGroupRepository = transactionGroupRepository;
    }

    public Transaction create(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }

    public TransactionGroup createGroup(TransactionGroup transactionGroup){
        if (transactionGroup.getUserId() == null){
            if (this.transactionGroupRepository.getByName(transactionGroup.getName()) != null){
                return null;
            }
        }else{
            if (this.transactionGroupRepository.getByUserId(transactionGroup.getUserId()).stream().anyMatch(group1 -> group1.getName().equalsIgnoreCase(transactionGroup.getName()))){
                return null;
            }
        }
        return this.transactionGroupRepository.save(transactionGroup);
    }

}
