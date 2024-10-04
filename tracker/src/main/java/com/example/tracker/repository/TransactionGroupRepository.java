package com.example.tracker.repository;

import com.example.tracker.model.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, Long> {
    List<TransactionGroup> getByUserId(Long userId);
    TransactionGroup getByName(String name);
}
