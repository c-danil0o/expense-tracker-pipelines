package com.example.tracker.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionGroupDTO {
    private Long id;
    private Double budgetCap;
    private String name;
    private Long userId;

    public String getBudgetCapSafe(){
        if (budgetCap != null)
            return budgetCap.toString();
        return "";
    }

}
