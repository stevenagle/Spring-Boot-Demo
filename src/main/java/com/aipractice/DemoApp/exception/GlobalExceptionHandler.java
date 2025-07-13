package com.aipractice.DemoApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<String> handleInvalidUserId(InvalidUserIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Only numbers are supported for user lookup & should be less than 12 digits.");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("That user does not exist. Please try again.");
    }
}

