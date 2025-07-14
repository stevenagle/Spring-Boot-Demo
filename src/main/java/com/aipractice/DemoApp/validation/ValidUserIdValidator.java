package com.aipractice.DemoApp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUserIdValidator implements ConstraintValidator<ValidUserId, String> {

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        return id != null && id.matches("^\\d{1,12}$");
    }
}
