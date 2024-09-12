package com.example.tracker.service.interfaces;

import com.example.tracker.utils.ReportTableRow;

import java.util.List;
import java.util.Map;

public interface ReportService {
    String generateReport(Long userId,int year);
//    Map<String, List<ReportTableRow>> getReportByMonthForYear(Long userId, int year);
}
