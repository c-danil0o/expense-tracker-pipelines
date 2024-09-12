package com.example.tracker.exceptions;

public class MailSendFailedException extends RuntimeException{
    public MailSendFailedException(String message){
        super(message);
    }
}
