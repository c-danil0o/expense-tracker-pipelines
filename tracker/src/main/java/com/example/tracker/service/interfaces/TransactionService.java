package com.example.tracker.service.interfaces;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.exceptions.TransactionGroupAlreadyExistsException;
import com.example.tracker.exceptions.TransactionGroupNotFoundException;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;

public interface TransactionService extends CrudService<TransactionDTO, Long> {
    TransactionGroupDTO createGroup(TransactionGroupDTO transactionGroup) throws TransactionGroupAlreadyExistsException;
    TransactionGroupDTO getGroupById(Long id) throws TransactionGroupNotFoundException;
}
