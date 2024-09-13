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
    private LocalDate nextRun;
    private int repeatRate;
    @OneToOne()
    private TransactionGroup group;
}
