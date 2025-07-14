package com.aipractice.DemoApp.controller;

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

    // GET endpoint fetches user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        UserProfileValidator.validateGetRequest(id);

        return userProfileService.getUserProfile(id);
    }

    // POST endpoint creates a new user profile
    @PostMapping("/users")
    public ResponseEntity<String> createResource(@RequestBody Map<String, String> request) {
        // Shoddy temp validation, will leverage spring validation in subsequent iterations when I add a real datasource back-end
        try {
            UserProfileValidator.validateCreateRequest(request);
            return userProfileService.createUserProfile(request);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
