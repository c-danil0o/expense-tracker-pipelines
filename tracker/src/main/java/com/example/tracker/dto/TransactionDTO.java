package com.example.tracker.dto;

import com.example.tracker.model.RepeatType;
import com.example.tracker.model.TransactionStatus;
import com.example.tracker.model.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionDTO {
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
