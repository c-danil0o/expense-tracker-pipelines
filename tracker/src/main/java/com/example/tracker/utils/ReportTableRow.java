package com.example.tracker.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class ReportTableRow {
    private String categoryName;
    private String expenses;
    private String incomes;
    private String total;
}
