package com.example.tracker.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Builder
@Setter
@Getter
public class ReportTableRow {
    private String date;
    private String time;
    private String currency;
    private String amount;
    private String status;
    private String category;
}
