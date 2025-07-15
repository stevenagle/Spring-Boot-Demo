package com.aipractice.DemoApp.unit.service;

import com.aipractice.DemoApp.TestData;
import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.exception.InvalidUpdateException;
import com.aipractice.DemoApp.exception.InvalidUserIdException;
import com.aipractice.DemoApp.exception.UserNotFoundException;
import com.aipractice.DemoApp.repository.UserProfileRepository;
import com.aipractice.DemoApp.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceUnitTest {

    @Mock
    private UserProfileRepository repository;

    @InjectMocks
    private UserProfileService service;

    // GET by ID
    @Test
    void getUserProfile_validId_shouldReturnUser() {
        UserProfile user = TestData.provideUserProfiles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = service.getUserProfile("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserProfile_invalidIdFormat_shouldReturnBadRequest() {
        ResponseEntity<?> response = service.getUserProfile("banana");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Only numbers are supported for user lookup & should be less than 12 digits.", response.getBody());
    }

    @Test
    void getUserProfile_nonExistentId_shouldReturnNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = service.getUserProfile("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("That user does not exist. Please try again.", response.getBody());
    }

    // GET all
    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<UserProfile> users = TestData.provideUserProfiles();
        when(repository.findAll()).thenReturn(users);

        ResponseEntity<?> response = service.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    void getAllUsers_emptyList_shouldReturnNoContent() {
        when(repository.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = service.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("No user profiles found in the database.", response.getBody());
    }

    @Test
    void getAllUsers_exception_shouldReturnServerError() {
        when(repository.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = service.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("A server error occurred while retrieving user profiles.", response.getBody());
    }

    // POST
    @Test
    void createUserProfile_shouldReturnSuccessMessage() {
        Map<String, String> payload = Map.of(
                "username", "newuser",
                "emailAddress", "newuser@example.com",
                "streetAddress", "123 New St",
                "city", "Newville",
                "state", "TX",
                "zipCode", "75001"
        );

        UserProfile savedUser = UserProfile.builder()
                .id(99L)
                .username("newuser")
                .emailAddress("newuser@example.com")
                .streetAddress("123 New St")
                .city("Newville")
                .state("TX")
                .zipCode("75001")
                .build();

        when(repository.save(any(UserProfile.class))).thenReturn(savedUser);

        ResponseEntity<String> response = service.createUserProfile(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("New user ID 99 created successfully."));
    }

    @Test
    void createUserProfile_exception_shouldReturnServerError() {
        Map<String, String> payload = Map.of(
                "username", "newuser",
                "emailAddress", "newuser@example.com",
                "streetAddress", "123 New St",
                "city", "Newville",
                "state", "TX",
                "zipCode", "75001"
        );

        when(repository.save(any(UserProfile.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<String> response = service.createUserProfile(payload);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while creating the user profile. Please try again later.", response.getBody());
    }

    // PATCH
    @Test
    void updateUserProfile_validField_shouldUpdateSuccessfully() {
        UserProfile user = TestData.provideUserProfiles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(UserProfile.class))).thenReturn(user);

        Map<String, String> payload = Map.of(
                "path", "city",
                "value", "UpdatedCity"
        );

        ResponseEntity<?> response = service.updateUserProfile("1", payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("city updated for user ID 1", response.getBody());
    }

    @Test
    void updateUserProfile_invalidId_shouldThrowInvalidUserIdException() {
        Map<String, String> payload = Map.of("path", "city", "value", "UpdatedCity");

        assertThrows(InvalidUserIdException.class, () ->
                service.updateUserProfile("banana", payload));
    }

    @Test
    void updateUserProfile_nonExistentUser_shouldThrowUserNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Map<String, String> payload = Map.of("path", "city", "value", "UpdatedCity");

        assertThrows(UserNotFoundException.class, () ->
                service.updateUserProfile("999", payload));
    }

    @Test
    void updateUserProfile_invalidField_shouldThrowInvalidUpdateException() {
        UserProfile user = TestData.provideUserProfiles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Map<String, String> payload = Map.of("path", "unknownField", "value", "oops");

        assertThrows(InvalidUpdateException.class, () ->
                service.updateUserProfile("1", payload));
    }

    // DELETE
    @Test
    void deleteUserProfile_validId_shouldDeleteSuccessfully() {
        UserProfile user = TestData.provideUserProfiles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(repository).deleteById(1L);

        ResponseEntity<?> response = service.deleteUserProfile("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUserProfile_invalidIdFormat_shouldReturnBadRequest() {
        ResponseEntity<?> response = service.deleteUserProfile("banana");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User ID should be a numeric value.", response.getBody());
    }

    @Test
    void deleteUserProfile_nonExistentUser_shouldThrowUserNotFoundException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                service.deleteUserProfile("999"));
    }

    @Test
    void deleteUserProfile_exception_shouldReturnServerError() {
        UserProfile user = TestData.provideUserProfiles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("DB error")).when(repository).deleteById(1L);

        ResponseEntity<?> response = service.deleteUserProfile("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server error occurred while deleting user profile.", response.getBody());
    }
}
