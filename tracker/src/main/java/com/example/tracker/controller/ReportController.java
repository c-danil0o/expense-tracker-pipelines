package com.example.tracker.controller;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.service.interfaces.ReportService;
import com.example.tracker.utils.ReportTableRow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReportController {
    private final ReportService reportService;


    @GetMapping(value = "/report/generate")
    public ResponseEntity<String> generateReport(@RequestParam Long userId, @RequestParam int year) {
        return ResponseEntity.ok(this.reportService.generateReport(userId, year));
    }

    @GetMapping(value = "/report/send-email")
    public ResponseEntity<Boolean> sendEmail(@RequestParam Long userId, @RequestParam int year) {
        return ResponseEntity.ok(this.reportService.sendEmail(userId, year));
    }

    @GetMapping(value = "/report/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam Long userId, @RequestParam int year) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return new ResponseEntity<>(this.reportService.downloadPdfReport(userId, year), header, HttpStatus.OK);
    }
}
