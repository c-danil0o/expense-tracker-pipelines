package com.example.tracker.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthData {
    private String name;
    private String total;
    private List<ReportTableRow> categories;
}
