package com.example.DemoApp.service;

import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.exception.InvalidUpdateException;
import com.example.DemoApp.exception.InvalidUserInputException;
import com.example.DemoApp.exception.UserAlreadyExistsException;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.repository.UserProfileRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> getUserProfile(String username) {
        try {
            return repository.findByUsername(username)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("That user does not exist. Please try again."));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username should be less than 32 characters and contain only letters and numbers.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("A server error occurred while retrieving the user profile.");
        }
    }

    public ResponseEntity<?> getAllUsers() {
        try {
            var allUsers = repository.findAll();

            if (allUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("No user profiles found in the database.");
            }

            return ResponseEntity.ok(allUsers);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("A server error occurred while retrieving user profiles.");
        }
    }

    public ResponseEntity<String> createUserProfile(Map<String, String> payload) {
        UserProfile user = UserProfile.builder()
                .username(payload.get("username"))
                .emailAddress(payload.get("emailAddress"))
                .streetAddress(payload.get("streetAddress"))
                .city(payload.get("city"))
                .state(payload.get("state"))
                .zipCode(payload.get("zipCode"))
                .build();

        try {
            UserProfile saved = repository.save(user);
            return ResponseEntity.ok("New user ID " + saved.getId() + " created successfully.");
        } catch (DataIntegrityViolationException dive) {
            throw new UserAlreadyExistsException();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating a user profile. Double-check your request.");
        }
    }

    public ResponseEntity<?> updateUserProfile(String username, Map<String, String> payload) {
        try {
            Optional<UserProfile> existingUserOpt = repository.findByUsername(username);

            if (existingUserOpt.isEmpty()) {
                throw new UserNotFoundException("No user found with username: " + username);
            }

            // Extract update info
            String field = payload.get("path");
            String newValue = payload.get("value");
            UserProfile user = existingUserOpt.get();

            // Apply update
            switch (field) {
                case "username" -> user.setUsername(newValue);
                case "emailAddress" -> user.setEmailAddress(newValue);
                case "streetAddress" -> user.setStreetAddress(newValue);
                case "city" -> user.setCity(newValue);
                case "state" -> user.setState(newValue);
                case "zipCode" -> user.setZipCode(newValue);
                default -> throw new InvalidUpdateException("Field '" + field + "' cannot be updated.");
            }

            repository.saveAndFlush(user);
            return ResponseEntity.ok(field + " updated for user: " + user.getUsername());

        } catch (DataIntegrityViolationException dive) {
            throw new UserAlreadyExistsException();
        } catch (InvalidUpdateException | UserNotFoundException ex) {
            throw ex; // Let GlobalExceptionHandler handle these
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error occurred while updating user profile.");
        }
    }

    public ResponseEntity<?> deleteUserProfile(String username) {
        try {
            Optional<UserProfile> userOpt = repository.findByUsername(username);

            if (userOpt.isEmpty()) {
                throw new UserNotFoundException("No user found with username: " + username);
            }
            UserProfile user = userOpt.get();
            repository.delete(user);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException ex) {
            throw ex; // Let GlobalExceptionHandler handle this
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error occurred while deleting user profile.");
        }
    }
}
