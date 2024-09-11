package com.example.tracker.controller;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = "/report/generate/{userId}")
    public ResponseEntity<String> generateReport(@PathVariable Long userId) {
        return ResponseEntity.ok(this.reportService.generateReport(userId));
    }
}
