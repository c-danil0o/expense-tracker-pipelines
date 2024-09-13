package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.*;

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
    // Last time reminder was sent to a user
    private LocalDateTime lastSent;
    private int repeatRate;
}
