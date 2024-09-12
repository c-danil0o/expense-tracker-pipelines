package com.example.tracker.controller;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.service.interfaces.ReportService;
import com.example.tracker.utils.ReportTableRow;
import lombok.RequiredArgsConstructor;
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
}
