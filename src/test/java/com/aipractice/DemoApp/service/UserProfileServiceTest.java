package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserProfileServiceTest {

    @Autowired
    private UserProfileRepository repository;

    @Test
    void testGetUserProfile_validIds_shouldReturnCorrectData() {
        UserProfileService service = new UserProfileService(repository);

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
        UserProfileService service = new UserProfileService(repository);
        ResponseEntity<?> response = service.getUserProfile("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("That user does not exist. Please try again.", response.getBody());
    }

    @Test
    void testGetUserProfile_invalidId_shouldReturn400() {
        UserProfileService service = new UserProfileService(repository);
        ResponseEntity<?> response = service.getUserProfile("banana");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only numbers are supported for user lookup & should be less than 12 digits.", response.getBody());
    }

    @Test
    void testCreateUserProfile_shouldPersistNewUser() {
        UserProfileService service = new UserProfileService(repository);

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
}
