package com.example.tracker.exceptions;

public class TransactionGroupAlreadyExists extends  Exception{
    public TransactionGroupAlreadyExists(String message)
    {
        super(message);
    }
}
