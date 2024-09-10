package com.example.tracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    private Long id;
    private Long userId;
    private LocalDateTime timestamp;
    private TransactionType type;
    private String currency;
    private double amount;
    private TransactionStatus status;
    private RepeatType repeatType;
    private Long transactionGroupId;
}

