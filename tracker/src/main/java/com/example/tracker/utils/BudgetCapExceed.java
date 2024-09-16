package com.example.tracker.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BudgetCapExceed {
    private String userEmail;
    private double budgetCap;
    private double amount;
    private String category;
}
