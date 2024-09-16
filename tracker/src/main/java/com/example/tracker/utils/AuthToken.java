package com.example.tracker.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuthToken {
    private String token;
    private Long expiration;
}
