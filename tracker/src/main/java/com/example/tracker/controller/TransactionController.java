package com.example.tracker.controller;

import com.example.tracker.exceptions.TransactionGroupAlreadyExists;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction)
    {
        this.transactionService.create(transaction);
        return ResponseEntity.ok(transaction);

    }

    @PostMapping(value = "/group", consumes = "application/json")
    public ResponseEntity<TransactionGroup> createTransactionGroup(@RequestBody TransactionGroup transactionGroup) throws TransactionGroupAlreadyExists {
        this.transactionService.createGroup(transactionGroup);
        return ResponseEntity.ok(transactionGroup);

    }
}
