package com.example.tracker.repository;

import com.example.tracker.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("SELECT r FROM Reminder r WHERE r.nextRun = :date")
    List<Reminder> getRemindersForDate(@Param("date") LocalDate date);
    @Query("SELECT r FROM Reminder r WHERE r.user.userId = :userId AND r.group.id = :transactionGroupId AND r.type = 'BudgetCap'")
    Optional<Reminder> findReminderByUserIdAndTransactionGroupIdBudgetCap(@Param("userId") Long userId, @Param("transactionGroupId") Long transactionGroupId);
}
