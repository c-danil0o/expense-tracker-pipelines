package com.example.tracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String email;
    private String firstName;
    private String lastName;
    private UserType type;
    private String currency;
    private double funds;
    private double reservedFunds;

}
