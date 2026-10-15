package com.example.DemoApp.unit.validation;

import com.example.DemoApp.TestData;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.dto.UserProfileRequest;
import com.example.DemoApp.validation.UserProfileValidator;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

/** Exercises input boundaries used by creation and partial updates. */
class UserProfileValidatorUnitTest {
    static ValidatorFactory factory;
    static UserProfileValidator validator;
    @BeforeAll static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = new UserProfileValidator(factory.getValidator());
    }
    @AfterAll static void tearDown() { factory.close(); }

    @ParameterizedTest
    @MethodSource("com.example.DemoApp.TestData#invalidProfileFields")
    void rejectsInvalidProfileFields(String field, String value) {
        assertThatThrownBy(() -> validator.validate(TestData.requestWith(field, value)))
                .isInstanceOfSatisfying(ConstraintViolationException.class, ex ->
                        assertThat(ex.getConstraintViolations()).anySatisfy(violation ->
                                assertThat(violation.getPropertyPath().toString()).isEqualTo(field)));
    }

    @ParameterizedTest
    @CsvSource({"username,a", "username,ABCDEFGHIJKLMNOPQRSTUVWXYZ012345", "state,IL", "zipCode,60601", "zipCode,60601-1234"})
    void acceptsValidFieldBoundaries(String field, String value) {
        validator.validate(TestData.requestWith(field, value));
    }

    @Test void acceptsMaximumLengths() {
        validator.validate(new UserProfileRequest("a".repeat(32), TestData.boundaryEmail(),
                "a".repeat(255), "a".repeat(100), "IL", "60601-1234"));
        validator.validate(new UpdateUserRequest("replace", "streetAddress", "a".repeat(255)));
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" ", "bad!", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void rejectsInvalidUsernames(String username) {
        assertThatThrownBy(() -> validator.validate(TestData.request(username))).isInstanceOf(ConstraintViolationException.class);
    }

    @Test void acceptsUsernameBoundaryAndExtendedZip() {
        validator.validate(new UserProfileRequest("a".repeat(32), "test+demo@example.com", "1 Main", "Chicago", "IL", "60601-1234"));
    }

    @ParameterizedTest @CsvSource({"remove,city,value", "replace,id,1", "replace,unknown,value", ",city,value", "'',city,value", "REPLACE,city,value", "replace,,value", "replace,'',value"})
    void rejectsUnsupportedOperations(String op, String path, String value) {
        assertThatThrownBy(() -> validator.validate(new UpdateUserRequest(op, path, value)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test void rejectsOversizedPatchValue() {
        assertThatThrownBy(() -> validator.validate(new UpdateUserRequest("replace", "streetAddress", "a".repeat(256))))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void rejectsMissingPatchValues(String value) {
        assertThatThrownBy(() -> validator.validate(new UpdateUserRequest("replace", "city", value)))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
