package com.example.tracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Reminder {
    private Long id;
    private ReminderType type;
    // Last time reminder was sent to a user
    private LocalDateTime lastCheck;
}
