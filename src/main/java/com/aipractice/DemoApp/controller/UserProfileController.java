package com.aipractice.DemoApp.controller;

import com.aipractice.DemoApp.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
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

    // GET endpoint to fetch a resource
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable String id) {
        Optional<UserProfile> profile = userProfileService.getUserProfile(id);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create or process a resource
    @PostMapping
    public ResponseEntity<String> createResource(@RequestBody Map<String, Object> payload) {
        // Process the incoming payload
        String name = payload.getOrDefault("name", "Unnamed").toString();
        return ResponseEntity.ok("Resource '" + name + "' created successfully.");
    }
}
