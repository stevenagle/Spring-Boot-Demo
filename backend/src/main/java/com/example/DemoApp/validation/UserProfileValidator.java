package com.example.DemoApp.validation;

import com.example.DemoApp.exception.InvalidUpdateException;
import com.example.DemoApp.exception.InvalidUserInputException;

import java.util.*;

/**
 * This is a temporary validator class as I'm playing around. When I start converting my model to @Entity and shimming in a real back-end (MySQL probably),
 * this class will be deleted and replaced with annotation-driven validation
 */

public class UserProfileValidator {

    private static final Set<String> REQUIRED_FIELDS = new LinkedHashSet<>(Arrays.asList(
            "username",
            "emailAddress",
            "streetAddress",
            "city",
            "state",
            "zipCode"
    ));

    public static void validateCreateRequest(Map<String, String> payload) throws IllegalArgumentException {
        for (String field : REQUIRED_FIELDS) {
            if (!payload.containsKey(field)) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
            String value = payload.get(field);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Field '" + field + "' cannot be blank.");
            }
        }

        validateEmail(payload.get("emailAddress"));
    }

    public static void validateUserId(String id) throws InvalidUserInputException {
        if (!id.matches("^\\d+$") || id.length() > 12) {
            throw new InvalidUserInputException("User ID should be numeric and less than 12 digits.");
        }
    }

    public static void validateEmail(String email) throws InvalidUserInputException {
        final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+(\\.[a-zA-Z0-9._-]+)*\\.[a-zA-Z]{2,}$";
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidUserInputException("Invalid email format. Only letters, digits, and . _ - are allowed. Email must include '@' and a valid domain.");
        }
    }

    public static void validatePatchRequest(Map<String, String> payload) throws InvalidUpdateException {
        String op = payload.get("op");
        String path = payload.get("path");

        if (!"replace".equalsIgnoreCase(op)) {
            throw new InvalidUpdateException("Unsupported operation: '" + op + "'. Only 'replace' is allowed.");
        }

        if (!REQUIRED_FIELDS.contains(path)) {
            String allowed = String.join(", ", REQUIRED_FIELDS);
            throw new InvalidUpdateException("Field '" + path + "' cannot be updated. Allowed fields: " + allowed);
        }
    }
}
