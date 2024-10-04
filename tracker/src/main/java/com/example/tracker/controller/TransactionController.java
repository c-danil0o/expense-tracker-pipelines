package com.example.tracker.controller;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.model.Transaction;
import com.example.tracker.service.interfaces.ReminderService;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class TransactionController {

    private final TransactionService transactionService;
    private final ReminderService reminderService;
    private final UserService userService;

    @PostMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDto) {
        TransactionDTO savedTransaction = this.transactionService.save(transactionDto);
        this.reminderService.sendNotificationIfBudgetCapExceeded(savedTransaction.getUserId(), savedTransaction.getTransactionGroupId());
        return ResponseEntity.ok(savedTransaction);
    }

    @PostMapping(value = "/group", consumes = "application/json")
    public ResponseEntity<TransactionGroupDTO> createTransactionGroup(@RequestBody TransactionGroupDTO transactionGroupDTO) {

        return ResponseEntity.ok(this.transactionService.createGroup(transactionGroupDTO));
    }

    @PutMapping(value = "/transaction")
    public ResponseEntity<TransactionDTO> updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        TransactionDTO savedTransaction = this.transactionService.update(transactionDTO);
        this.reminderService.sendNotificationIfBudgetCapExceeded(savedTransaction.getUserId(), savedTransaction.getTransactionGroupId());
        return ResponseEntity.ok(savedTransaction);
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

//    @GetMapping(value = "/transaction/all")
//    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
//        return ResponseEntity.ok(this.transactionService.findAll());
//    }


    @GetMapping(value = "/transaction/query")
    public ResponseEntity<List<TransactionDTO>> queryTransactions(@RequestParam(required = false) LocalDateTime startDate,
                                                                  @RequestParam(required = false) LocalDateTime endDate,
                                                                  @RequestParam(required = false) String type,
                                                                  @RequestParam(required = false) String currency,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer pageSize,
                                                                  @RequestParam(required = false) String sortParam) {

        return ResponseEntity.ok(this.transactionService.query(startDate, endDate, type, currency, category, status, page, pageSize, sortParam));
    }
}

