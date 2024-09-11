package com.example.tracker.dto;

import com.example.tracker.model.RepeatType;
import com.example.tracker.model.TransactionStatus;
import com.example.tracker.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
public class TransactionDTO {
    private Long id;
    private Long userId;
    @DateTimeFormat()
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private TransactionType type;
    private String currency;
    private double amount;
    private TransactionStatus status;
    private RepeatType repeatType;
    private Long transactionGroupId;
}
