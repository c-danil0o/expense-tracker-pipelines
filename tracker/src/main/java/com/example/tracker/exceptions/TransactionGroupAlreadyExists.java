package com.example.tracker.exceptions;

public class TransactionGroupAlreadyExists extends  RuntimeException{
    public TransactionGroupAlreadyExists(String message)
    {
        super(message);
    }
}
