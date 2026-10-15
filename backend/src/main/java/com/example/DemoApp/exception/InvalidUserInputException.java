package com.example.DemoApp.exception;

/** Signals invalid profile input. */
public class InvalidUserInputException extends RuntimeException {
    public InvalidUserInputException(String message) {
        super(message);
    }
}