package com.example.tracker.dto;

import com.example.tracker.model.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String currency;
    private UserType type;
    private double funds;
    private double reservedFunds;
}
