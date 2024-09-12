package com.example.tracker.controller;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDto) {
        return ResponseEntity.ok(this.transactionService.save(transactionDto));
    }

    @PostMapping(value = "/group", consumes = "application/json")
    public ResponseEntity<TransactionGroupDTO> createTransactionGroup(@RequestBody TransactionGroupDTO transactionGroupDTO) {

        return ResponseEntity.ok(this.transactionService.createGroup(transactionGroupDTO));
    }

    @PutMapping(value = "/transaction")
    public ResponseEntity<TransactionDTO> updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(this.transactionService.update(transactionDTO));
    }

    @DeleteMapping(value = "/transaction/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        this.transactionService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(this.transactionService.findById(id));
    }

    @GetMapping(value = "/transaction/all")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(this.transactionService.findAll());
    }
}
