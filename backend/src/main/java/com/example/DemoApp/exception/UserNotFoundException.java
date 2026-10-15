package com.example.DemoApp.exception;

/** Signals a missing profile. */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}