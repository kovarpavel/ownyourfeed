package com.kovarpavel.ownyourfeed.exception;

public class SourceNotFoundException extends RuntimeException{
    public SourceNotFoundException(String message) {
        super(message);
    }
}
