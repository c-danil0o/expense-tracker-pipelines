package com.example.tracker.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionGroupDTO {
    private Long id;
    private double budgetCap;
    private String name;
    private Long userId;

}
