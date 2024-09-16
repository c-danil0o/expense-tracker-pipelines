package com.example.tracker.exceptions;

public class InvalidTransactionGroupException extends RuntimeException{
    public InvalidTransactionGroupException(String message){
        super(message);
    }
}
