package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.exception.InvalidUpdateException;
import com.aipractice.DemoApp.exception.InvalidUserIdException;
import com.aipractice.DemoApp.exception.UserNotFoundException;
import com.aipractice.DemoApp.repository.UserProfileRepository;
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

    public ResponseEntity<?> getUserProfile(String idString) {
        try {
            Long id = Long.parseLong(idString);

            return repository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("That user does not exist. Please try again."));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Only numbers are supported for user lookup & should be less than 12 digits.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("A server error occurred while retrieving the user profile.");
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
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the user profile. Please try again later.");
        }
    }

    public ResponseEntity<?> updateUserProfile(String userId, Map<String, String> payload) {
        try {
            Long id = Long.parseLong(userId);
            Optional<UserProfile> existingUserOpt = repository.findById(id);

            if (existingUserOpt.isEmpty()) {
                throw new UserNotFoundException("No user found with ID " + id);
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

            repository.save(user);
            return ResponseEntity.ok(field + " updated for user ID " + id);

        } catch (NumberFormatException e) {
            throw new InvalidUserIdException(e.getMessage());
        } catch (InvalidUpdateException | UserNotFoundException ex) {
            throw ex; // Let GlobalExceptionHandler handle these
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error occurred while updating user profile.");
        }
    }
}
