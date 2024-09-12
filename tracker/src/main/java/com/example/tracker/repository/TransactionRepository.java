package com.example.tracker.repository;

import com.example.tracker.model.Transaction;
import com.example.tracker.utils.MonthData;
import com.example.tracker.utils.ReportTableRow;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT tg.name AS categoryName, " +
            "MONTH(t.timestamp) AS month, " +
            "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS incomes, " +
            "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenses, " +
            "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS total " +
            "FROM Transaction t " +
            "JOIN t.transactionGroup tg " +
            "JOIN t.user user " +
            "WHERE YEAR(t.timestamp) = :inputYear AND user.userId = :userId " +
            "GROUP BY tg.name, MONTH(t.timestamp) " +
            "ORDER BY month"
    )
    List<Object []> getMonthlyReport(Long userId, int inputYear);
}
