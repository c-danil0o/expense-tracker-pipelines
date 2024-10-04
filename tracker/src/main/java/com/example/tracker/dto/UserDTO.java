package com.example.tracker.dto;

import com.example.tracker.model.Gender;
import com.example.tracker.model.UserType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String currency;
    private UserType type;
    private String country;
    private double funds;
    private double reservedFunds;
    private String password;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDateTime registeredAt;
}
