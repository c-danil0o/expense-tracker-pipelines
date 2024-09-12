package com.example.tracker.exceptions;

public class TransactionGroupNotFoundException extends  RuntimeException{
    public TransactionGroupNotFoundException(String message){
        super(message);
    }
}
