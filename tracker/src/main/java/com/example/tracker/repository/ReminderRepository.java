package com.example.tracker.repository;

import com.example.tracker.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("SELECT r FROM Reminder r WHERE r.lastSent = :date")
    List<Reminder> getRemindersForDate(@Param("date") LocalDate date);
}
