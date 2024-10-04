package com.example.tracker.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EmailReminder {
    private LocalDate startDate;
    private LocalDate endDate;
    private String user;
    private double spent;
}
