package com.example.tracker.service;

import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.TransactionGroupAlreadyExists;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import com.example.tracker.service.interfaces.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionGroupRepository transactionGroupRepository;


    public TransactionGroup createGroup(TransactionGroup transactionGroup) throws TransactionGroupAlreadyExists {
        if (transactionGroup.getUserId() == null){
            if (this.transactionGroupRepository.getByName(transactionGroup.getName()) != null){
                 throw new TransactionGroupAlreadyExists("Group with given name already exists!");
            }
        }else{
            if (this.transactionGroupRepository.getByUserId(transactionGroup.getUserId()).stream().anyMatch(group1 -> group1.getName().equalsIgnoreCase(transactionGroup.getName()))){
                throw new TransactionGroupAlreadyExists("Group with given name already exists!");
            }
        }
        return this.transactionGroupRepository.save(transactionGroup);
    }

    @Override
    public List<Transaction> findAll() {
        return this.transactionRepository.findAll();
    }

    @Override
    public Transaction findById(Long transactionId) throws ElementNotFoundException {
        return this.transactionRepository.findById(transactionId).orElseThrow(() -> new ElementNotFoundException("No such element with given id!"));
    }

    @Override
    public Transaction save(Transaction object) {
        return this.transactionRepository.save(object);
    }

    @Override
    public Transaction update(Transaction newTransaction) throws ElementNotFoundException {
        Transaction transaction = this.transactionRepository.findById(newTransaction.getId()).orElseThrow(() -> new ElementNotFoundException("No such element with given id!"));
        transaction.setAmount(newTransaction.getAmount());
        transaction.setTransactionGroup(newTransaction.getTransactionGroup());
        transaction.setTimestamp(newTransaction.getTimestamp());
        transaction.setStatus(newTransaction.getStatus());
        transaction.setRepeatType(newTransaction.getRepeatType());
        transaction.setUser(newTransaction.getUser());
        transaction.setType(newTransaction.getType());
        transaction.setCurrency(newTransaction.getCurrency());

        return this.transactionRepository.save(transaction);
    }

    @Override
    public void delete(Long transactionId) throws ElementNotFoundException {
        if (this.transactionRepository.existsById(transactionId)){
            this.transactionRepository.deleteById(transactionId);
        }else{
            throw new ElementNotFoundException("No such element with given ID!");
        }


    }
}
