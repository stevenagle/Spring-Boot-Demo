package com.aipractice.DemoApp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class PathFieldValidator implements ConstraintValidator<ValidPatchPath, String> {
    private static final Set<String> allowedPaths = Set.of(
            "username", "emailAddress", "streetAddress", "city", "state", "zipCode"
    );

    @Override
    public boolean isValid(String path, ConstraintValidatorContext context) {
        return allowedPaths.contains(path);
    }
}
