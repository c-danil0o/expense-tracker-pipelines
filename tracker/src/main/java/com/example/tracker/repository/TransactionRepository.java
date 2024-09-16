package com.example.tracker.repository;

import com.example.tracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
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
    @Query("SELECT SUM(t.amount) AS totalSpent " +
            "FROM Transaction t " +
            "JOIN t.user user " +
            "WHERE t.type = 'EXPENSE' " +
            "AND user.userId = :userId " +
            "AND t.timestamp BETWEEN :startDate AND :endDate"
    )
    Double getTotalSpentForUserInTimePeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT SUM(t.amount) AS totalSpent " +
            "FROM Transaction t " +
            "JOIN t.user user " +
            "JOIN t.transactionGroup tg " +
            "WHERE t.type = 'EXPENSE' " +
            "AND user.userId = :userId " +
            "AND tg.id = :transactionGroupId " +
            "AND t.timestamp BETWEEN :startDate AND :endDate"
    )
    Double getTotalSpentForUserInTimePeriodForTransactionGroup(Long userId, LocalDateTime startDate, LocalDateTime endDate, Long transactionGroupId);
}
