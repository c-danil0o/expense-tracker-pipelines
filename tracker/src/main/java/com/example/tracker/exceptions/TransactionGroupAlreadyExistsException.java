package com.example.tracker.exceptions;

public class TransactionGroupAlreadyExistsException extends  RuntimeException{
    public TransactionGroupAlreadyExistsException(String message)
    {
        super(message);
    }
}
