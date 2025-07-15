package com.aipractice.DemoApp.exception;

import com.aipractice.DemoApp.dto.ValidationErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
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

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<String> handleInvalidUpdate(InvalidUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<ValidationErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
//        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
//                .map(err -> err.getField() + ": " + err.getDefaultMessage())
//                .toList();
//
//        return ResponseEntity.badRequest()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(new ValidationErrorResponse("Validation failure", errors));
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//        MethodArgumentNotValidException ex,
//        HttpHeaders headers,
//        HttpStatusCode status,
//        WebRequest request
//    ) {
//        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
//            .map(err -> err.getField() + ": " + err.getDefaultMessage())
//            .toList();
//
//        ValidationErrorResponse response = new ValidationErrorResponse("Validation failure", errors);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
}

