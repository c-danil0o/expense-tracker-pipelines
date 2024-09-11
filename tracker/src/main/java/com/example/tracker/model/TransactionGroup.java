package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TransactionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double budgetCap;
    @Column(nullable = false)
    private String name;
    // Optional field for user created transaction groups
    private Long userId;

}
