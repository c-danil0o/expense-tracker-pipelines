package com.example.tracker.utils;

import com.example.tracker.model.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class MonthData {
    private String name;
    private List<ReportTableRow> transactions;
}
