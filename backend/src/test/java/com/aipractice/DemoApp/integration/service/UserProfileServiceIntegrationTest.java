package com.aipractice.DemoApp.integration.service;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.exception.InvalidUpdateException;
import com.aipractice.DemoApp.exception.UserNotFoundException;
import com.aipractice.DemoApp.repository.UserProfileRepository;
import com.aipractice.DemoApp.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserProfileServiceIntegrationTest {

    @Autowired
    private UserProfileRepository repository;

    @InjectMocks
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileService(repository);
    }

    // GET tests

    @Test
    void testGetUserProfile_validIds_shouldReturnCorrectData() {
        ResponseEntity<?> response1 = service.getUserProfile("1");
        ResponseEntity<?> response2 = service.getUserProfile("2");
        ResponseEntity<?> response3 = service.getUserProfile("3");

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertTrue(response1.getBody() instanceof UserProfile);
        assertEquals("alpha001", ((UserProfile) response1.getBody()).getUsername());

        assertEquals("bravo002", ((UserProfile) response2.getBody()).getUsername());
        assertEquals("charlie003", ((UserProfile) response3.getBody()).getUsername());
    }

    @Test
    void testGetUserProfile_nonExistentId_shouldReturn404() {
        ResponseEntity<?> response = service.getUserProfile("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("That user does not exist. Please try again.", response.getBody());
    }

    @Test
    void testGetUserProfile_invalidId_shouldReturn400() {
        ResponseEntity<?> response = service.getUserProfile("banana");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only numbers are supported for user lookup & should be less than 12 digits.", response.getBody());
    }

    @Test
    void shouldReturnAllUsersSuccessfully() {
        ResponseEntity<?> response = service.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());

        List<?> results = (List<?>) response.getBody();
        assertEquals(10, results.size()); // Or however many you expect
    }

    // POST tests

    @Test
    void testCreateUserProfile_shouldPersistNewUser() {
        var payload = Map.of(
                "username", "zulu011",
                "emailAddress", "zulu011@example.com",
                "streetAddress", "1111 Zulu St",
                "city", "Fleetwood",
                "state", "PA",
                "zipCode", "19522"
        );

        ResponseEntity<String> response = service.createUserProfile(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("New user ID"));
        assertTrue(response.getBody().contains("created successfully."));
    }

    // PATCH tests

    @ParameterizedTest
    @CsvSource({
            "username, UpdatedUser001",
            "emailAddress, updated@example.com",
            "streetAddress, 0987 Main St",
            "city, Gotham",
            "state, NY",
            "zipCode, 99999"
    })
    void testUpdateUserProfile_shouldUpdateField(String path, String value) {
        Map<String, String> payload = Map.of(
                "op", "replace",
                "path", path,
                "value", value
        );

        ResponseEntity<?> response = service.updateUserProfile("1", payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(path + " updated for user ID 1", response.getBody());

        Optional<UserProfile> updated = repository.findById(1L);
        assertTrue(updated.isPresent());

        UserProfile user = updated.get();
        switch (path) {
            case "username" -> assertEquals(value, user.getUsername());
            case "emailAddress" -> assertEquals(value, user.getEmailAddress());
            case "streetAddress" -> assertEquals(value, user.getStreetAddress());
            case "city" -> assertEquals(value, user.getCity());
            case "state" -> assertEquals(value, user.getState());
            case "zipCode" -> assertEquals(value, user.getZipCode());
        }
    }

    @Test
    void testUpdateUserProfile_nonExistentId_shouldReturn404() {
        Map<String, String> updatePayload = Map.of(
                "op", "replace",
                "path", "city",
                "value", "Ghosttown"
        );

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                service.updateUserProfile("999", updatePayload)
        );
        assertEquals("No user found with ID 999", ex.getMessage());
    }

    @Test
    void testUpdateUserProfile_invalidField_shouldThrowInvalidUpdateException() {
        Map<String, String> updatePayload = Map.of(
                "op", "replace",
                "path", "unknownField",
                "value", "SomeValue"
        );

        InvalidUpdateException ex = assertThrows(InvalidUpdateException.class, () ->
                service.updateUserProfile("1", updatePayload)
        );

        assertEquals("Field 'unknownField' cannot be updated.", ex.getMessage());
    }

    // DELETE tests

    @Test
    void testDeleteUserProfile_shouldRemoveUser() {
        // Confirm user exists
        Optional<UserProfile> before = repository.findById(1L);
        assertTrue(before.isPresent());

        ResponseEntity<?> response = service.deleteUserProfile("1");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Optional<UserProfile> after = repository.findById(1L);
        assertTrue(after.isEmpty());
    }

    @Test
    void testDeleteUserProfile_nonExistentId_shouldThrowException() {
        assertThrows(UserNotFoundException.class, () ->
                service.deleteUserProfile("999")
        );
    }
}
