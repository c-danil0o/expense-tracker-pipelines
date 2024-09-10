package com.example.tracker.service.interfaces;

import com.example.tracker.exceptions.TransactionGroupAlreadyExists;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;

public interface TransactionService extends CrudService<Transaction, Long> {
    TransactionGroup createGroup(TransactionGroup transactionGroup) throws TransactionGroupAlreadyExists;
}
