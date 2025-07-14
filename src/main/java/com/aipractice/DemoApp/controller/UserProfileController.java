package com.aipractice.DemoApp.controller;

import com.aipractice.DemoApp.exception.InvalidUpdateException;
import com.aipractice.DemoApp.exception.UserNotFoundException;
import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.validation.UserProfileValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aipractice.DemoApp.service.UserProfileService;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/demo")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // GET route fetches a single user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        try {
            UserProfileValidator.validateUserId(id);
            return userProfileService.getUserProfile(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // POST route creates a new user profile
    @PostMapping("/users")
    public ResponseEntity<String> createUserProfile(@RequestBody Map<String, String> request) {
        try {
            UserProfileValidator.validateCreateRequest(request);
            return userProfileService.createUserProfile(request);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //PATCH route allows updating a single user profile value
    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, @RequestBody Map<String,String> request) {
        try {
            UserProfileValidator.validatePatchRequest(request);
            return userProfileService.updateUserProfile(id, request);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // DELETE route deletes a user profile record
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserProfile(@PathVariable String id) {
        try {
            UserProfileValidator.validateUserId(id);
            return userProfileService.deleteUserProfile(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
