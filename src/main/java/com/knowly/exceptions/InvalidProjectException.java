package com.knowly.exceptions;

public class InvalidProjectException extends RuntimeException {
    public InvalidProjectException(String message) {
        super(message);
    }
}
