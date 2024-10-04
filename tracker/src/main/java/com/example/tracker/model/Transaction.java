package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String name;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String currency;
    private double amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;
    @JoinColumn(nullable = false, name = "transaction_group")
    @ManyToOne
    private TransactionGroup transactionGroup;
}

