package com.example.tracker.controller;

import com.example.tracker.exceptions.TransactionGroupAlreadyExists;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.service.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction)
    {
        return ResponseEntity.ok(this.transactionService.save(transaction));
    }

    @PostMapping(value = "/group", consumes = "application/json")
    public ResponseEntity<TransactionGroup> createTransactionGroup(@RequestBody TransactionGroup transactionGroup) {

        return ResponseEntity.ok(this.transactionService.createGroup(transactionGroup));

    }
}
