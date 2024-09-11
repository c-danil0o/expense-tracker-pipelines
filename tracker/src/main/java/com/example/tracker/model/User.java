package com.example.tracker.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private UserType type;
    private String currency;
    private double funds;
    private double reservedFunds;

}
