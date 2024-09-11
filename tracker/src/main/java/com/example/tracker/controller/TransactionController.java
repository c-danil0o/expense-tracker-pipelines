package com.example.tracker.controller;

import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.service.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping(value = "/transaction")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody Transaction transaction){
        return ResponseEntity.ok(this.transactionService.update(transaction));
    }

    @DeleteMapping(value = "/transaction/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id){
        this.transactionService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id){
        return ResponseEntity.ok(this.transactionService.findById(id));
    }

    @GetMapping(value = "/transaction/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(this.transactionService.findAll());
    }
}
