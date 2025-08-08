package com.aipractice.DemoApp.unit.validation;

import com.aipractice.DemoApp.exception.InvalidUpdateException;
import com.aipractice.DemoApp.exception.InvalidUserInputException;
import com.aipractice.DemoApp.validation.UserProfileValidator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileValidatorTest {

    // Create user validations
    @Test
    void validateCreateRequest_validPayload_shouldPass() {
        Map<String, String> payload = Map.of(
                "username", "validuser",
                "emailAddress", "valid.user@example.com",
                "streetAddress", "123 Main St",
                "city", "Chicago",
                "state", "IL",
                "zipCode", "60601"
        );

        assertDoesNotThrow(() -> UserProfileValidator.validateCreateRequest(payload));
    }

    @Test
    void validateCreateRequest_missingField_shouldThrow() {
        Map<String, String> payload = Map.of(
                "username", "validuser",
                "emailAddress", "valid.user@example.com",
                "streetAddress", "123 Main St",
                "city", "Chicago",
                "state", "IL"
                // zipCode missing
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> UserProfileValidator.validateCreateRequest(payload));
        assertEquals("Missing required field: zipCode", ex.getMessage());
    }

    @Test
    void validateCreateRequest_blankField_shouldThrow() {
        Map<String, String> payload = Map.of(
                "username", "validuser",
                "emailAddress", "valid.user@example.com",
                "streetAddress", "123 Main St",
                "city", "Chicago",
                "state", "IL",
                "zipCode", "   "
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> UserProfileValidator.validateCreateRequest(payload));
        assertEquals("Field 'zipCode' cannot be blank.", ex.getMessage());
    }

    @Test
    void validateCreateRequest_invalidEmail_shouldThrow() {
        Map<String, String> payload = Map.of(
                "username", "validuser",
                "emailAddress", "invalid-email",
                "streetAddress", "123 Main St",
                "city", "Chicago",
                "state", "IL",
                "zipCode", "60601"
        );

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> UserProfileValidator.validateCreateRequest(payload));
        assertEquals("Invalid email format. Only letters, digits, and . _ - are allowed. Email must include '@' and a valid domain.", ex.getMessage());
    }

    // User ID validations
    @Test
    void validateUserId_valid_shouldPass() {
        assertDoesNotThrow(() -> UserProfileValidator.validateUserId("123456"));
    }

    @Test
    void validateUserId_nonNumeric_shouldThrow() {
        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> UserProfileValidator.validateUserId("abc123"));
        assertEquals("User ID should be numeric and less than 12 digits.", ex.getMessage());
    }

    @Test
    void validateUserId_tooLong_shouldThrow() {
        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> UserProfileValidator.validateUserId("1234567890123"));
        assertEquals("User ID should be numeric and less than 12 digits.", ex.getMessage());
    }

    // Email matching
    @Test
    void validateEmail_valid_shouldPass() {
        assertDoesNotThrow(() -> UserProfileValidator.validateEmail("user.name@example.co.uk"));
    }

    @Test
    void validateEmail_invalid_shouldThrow() {
        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> UserProfileValidator.validateEmail("user@@example..com"));
        assertEquals("Invalid email format. Only letters, digits, and . _ - are allowed. Email must include '@' and a valid domain.", ex.getMessage());
    }

    // Update user validations
    @Test
    void validatePatchRequest_valid_shouldPass() {
        Map<String, String> payload = Map.of(
                "op", "replace",
                "path", "city"
        );

        assertDoesNotThrow(() -> UserProfileValidator.validatePatchRequest(payload));
    }

    @Test
    void validatePatchRequest_invalidOp_shouldThrow() {
        Map<String, String> payload = Map.of(
                "op", "delete",
                "path", "city"
        );

        InvalidUpdateException ex = assertThrows(InvalidUpdateException.class,
                () -> UserProfileValidator.validatePatchRequest(payload));
        assertEquals("Unsupported operation: 'delete'. Only 'replace' is allowed.", ex.getMessage());
    }

    @Test
    void validatePatchRequest_invalidPath_shouldThrow() {
        Map<String, String> payload = Map.of(
                "op", "replace",
                "path", "unknownField"
        );

        InvalidUpdateException ex = assertThrows(InvalidUpdateException.class,
                () -> UserProfileValidator.validatePatchRequest(payload));
        assertEquals("Field 'unknownField' cannot be updated. Allowed fields: username, emailAddress, streetAddress, city, state, zipCode", ex.getMessage());
    }
}