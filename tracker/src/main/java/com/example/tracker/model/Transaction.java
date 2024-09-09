package com.example.tracker.model;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String userId;
    private LocalDateTime timestamp;
    private TransactionType type;
    private String currency;
    private double amount;
    private TransactionStatus status;
    private RepeatType repeatType;
}
