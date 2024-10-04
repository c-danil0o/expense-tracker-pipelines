package com.example.tracker.exceptions;

public class ReportDownloadFailedException extends RuntimeException{
    public ReportDownloadFailedException(String message){
        super(message);
    }
}
