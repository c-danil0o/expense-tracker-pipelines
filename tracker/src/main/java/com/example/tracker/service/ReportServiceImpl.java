package com.example.tracker.service;

import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.MailSendFailedException;
import com.example.tracker.model.Transaction;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.interfaces.ReportService;
import com.example.tracker.utils.HtmlPdfGenerator;
import com.example.tracker.utils.MonthData;
import com.example.tracker.utils.ReportTableRow;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final MailService mailService;

    @Override
    public String generateReport(Long userId, int year) {
        HtmlPdfGenerator pdfGenerator = new HtmlPdfGenerator();
        Map<String, Object> data = new HashMap<>();
        List<MonthData> months = new ArrayList<>();
        for (Map.Entry<String, List<ReportTableRow>> entry : this.getReportByMonthForYear(userId, year).entrySet()){
            double monthTotal = entry.getValue().stream().map(x -> Double.parseDouble(x.getTotal())).reduce(0.0, Double::sum);
            months.add(new MonthData(numberToMonth(Integer.parseInt(entry.getKey())),String.valueOf(monthTotal), entry.getValue()));
        }
        data.put("months", months);
        data.put("user", this.userRepository.findById(userId).orElseThrow(()-> new ElementNotFoundException("User not found!")).getEmail());
        String htmlData = pdfGenerator.parseReportTemplate(data);
        return pdfGenerator.parseReportTemplate(data);
    }

    public void deleteGeneratedPdf(){
        String fileName="report.pdf";
        try{
            Path filePath = Paths.get(fileName);
            Files.deleteIfExists(filePath);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void generatePdfFromReport(String htmlData){
        HtmlPdfGenerator pdfGenerator = new HtmlPdfGenerator();
        pdfGenerator.generatePdfFromHtml(htmlData);
    }

    public boolean sendEmail(Long userId, int year){
        String htmlData = this.generateReport(userId, year);
        String userEmail = this.userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("User not found!")).getEmail();
        try {
            this.mailService.sendHtmlEmail(userEmail, htmlData, "Expense tracker report for year " + year);
        } catch (MessagingException e) {
            throw new MailSendFailedException("Sending email failed for user " + userEmail);
        }
        return true;
    }

    @Override
    public ByteArrayResource downloadPdfReport(Long userId, int year) {
        String htmlData = this.generateReport(userId, year);
        this.generatePdfFromReport(htmlData);

        File file = new File("report.pdf");
        Path path = Paths.get(file.getAbsolutePath());
        try {
            return new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private Map<String, List<ReportTableRow>> getReportByMonthForYear(Long userId, int year) {
        Map<String, List<ReportTableRow>> report = new HashMap<>();
        List<Object[]> result = this.transactionRepository.getMonthlyReport(userId, year);
        for (Object[] resultRow : result) {
            if (report.containsKey(String.valueOf(resultRow[1]))){
               report.get(String.valueOf(resultRow[1])).add(new ReportTableRow(String.valueOf(resultRow[0]), String.valueOf(resultRow[3]), String.valueOf(resultRow[2]), String .valueOf(resultRow[4])));
            }else{
                List<ReportTableRow> row = new ArrayList<>();
                row.add(new ReportTableRow(String.valueOf(resultRow[0]), String.valueOf(resultRow[3]), String.valueOf(resultRow[2]), String .valueOf(resultRow[4])));
                report.put(String.valueOf(resultRow[1]),row);
            }
        }
        return report;
    }
    private  String numberToMonth(int monthNumber) {
        if (monthNumber < 1 || monthNumber > 12) {
            throw new IllegalArgumentException("Month number must be between 1 and 12");
        }

        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        return months[monthNumber - 1];
    }
}
