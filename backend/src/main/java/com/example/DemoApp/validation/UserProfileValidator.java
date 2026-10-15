package com.example.DemoApp.validation;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

/** Enforces the same Bean Validation rules for service callers and HTTP requests. */
@Component
public class UserProfileValidator {
    private final Validator validator;

    public UserProfileValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T value) {
        var violations = validator.validate(value);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
