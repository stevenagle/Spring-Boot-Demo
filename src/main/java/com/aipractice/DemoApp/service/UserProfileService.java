package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.domain.UserProfile;
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
}
