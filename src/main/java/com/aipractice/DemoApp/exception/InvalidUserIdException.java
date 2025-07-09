package com.aipractice.DemoApp.exception;

public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException(String message) {
        super(message);
    }
}