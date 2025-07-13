package com.aipractice.DemoApp.validation;

import java.util.Map;
import java.util.Set;

/**
 * This is a temporary validator class as I'm playing around. When I start converting my model to @Entity and shimming in a real back-end (MySQL probably),
 * this class will be deleted and replaced with annotation-driven validation
 */


public class UserProfileValidator {

    private static final Set<String> REQUIRED_FIELDS = Set.of(
            "username",
            "emailAddress",
            "streetAddress",
            "city",
            "state",
            "zipCode"
    );

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
    }
}
