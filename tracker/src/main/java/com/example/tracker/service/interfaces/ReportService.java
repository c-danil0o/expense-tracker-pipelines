package com.example.tracker.service.interfaces;
import org.springframework.core.io.ByteArrayResource;


public interface ReportService {
    String generateReport(Long userId,int year);
    boolean sendEmail(Long userId, int year);
    ByteArrayResource downloadPdfReport(Long userId, int year);
}
