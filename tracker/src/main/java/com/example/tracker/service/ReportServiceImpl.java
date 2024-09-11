package com.example.tracker.service;

import com.example.tracker.model.Transaction;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.ReportService;
import com.example.tracker.utils.HtmlPdfGenerator;
import com.example.tracker.utils.MonthData;
import com.example.tracker.utils.ReportTableRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final TransactionRepository transactionRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final UserRepository userRepository;

    @Override
    public String generateReport() {
        HtmlPdfGenerator pdfGenerator = new HtmlPdfGenerator();

        Map<String, Object> data = new HashMap<>();
        List<MonthData> months = new ArrayList<>();
        List<Transaction> transactions = this.transactionRepository.findAll();
        MonthData jan = new MonthData();
        jan.setName("January");
        List<ReportTableRow> rows = transactions.stream().map(tr -> {
             return ReportTableRow.builder().date(tr.getTimestamp().format(DateTimeFormatter.ISO_DATE))
                    .time(tr.getTimestamp().format(DateTimeFormatter.ISO_TIME)).currency(tr.getCurrency())
                    .amount(String.valueOf(tr.getAmount())).status(tr.getStatus().toString()).
                    category(tr.getTransactionGroup().getName()).build();
        }).toList();
        jan.setTransactions(rows);
        months.add(jan);
        data.put("months", months);
        return pdfGenerator.parseReportTemplate(data);
    }
}
