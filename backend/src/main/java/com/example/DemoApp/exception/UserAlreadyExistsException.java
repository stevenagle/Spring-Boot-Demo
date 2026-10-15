package com.example.DemoApp.exception;

/** Signals a conflicting username. */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super();
    }
}
