package com.example.tracker.exceptions;

public class UserRegistrationException extends RuntimeException{
    public UserRegistrationException(String message){
        super(message);
    }
}
