package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ReminderType type;
    @ManyToOne
    private User user;
    // Next time reminder will be sent to a user
    // Not present in BudgetCap reminders
    private LocalDate nextRun;
    private int repeatRate;
    // if transactionGroup is not null and reminder type is BudgetCap reminder is activated when adding expense transaction
    @OneToOne()
    private TransactionGroup group;
}
