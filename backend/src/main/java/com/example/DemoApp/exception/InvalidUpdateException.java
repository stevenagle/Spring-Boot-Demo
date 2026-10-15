package com.example.DemoApp.exception;

/** Signals an unsupported profile update. */
public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(String message) { super(message); }
}

