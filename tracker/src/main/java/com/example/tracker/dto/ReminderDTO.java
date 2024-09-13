package com.example.tracker.dto;

import com.example.tracker.model.ReminderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReminderDTO {
    private Long id;
    private ReminderType type;
    private LocalDateTime lastSent;
    private int repeatRate;
    private Long userId;
}
