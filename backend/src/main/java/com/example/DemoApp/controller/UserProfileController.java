package com.example.DemoApp.controller;

import com.example.DemoApp.dto.UserProfileRequest;
import com.example.DemoApp.dto.UserProfileResponse;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** Exposes the demo profile resource and translates service results into HTTP responses. */
@Tag(name = "User Profile API", description = "Demo profile CRUD; no authentication")
@RestController
@RequestMapping("/api/v1/demo/users")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @Operation(summary = "Find a profile by username")
    @GetMapping("/{username}")
    public UserProfileResponse getUserProfile(
            @PathVariable @Pattern(regexp = "[A-Za-z0-9]{1,32}") String username) {
        return service.getUserProfile(username);
    }

    @Operation(summary = "List demo profiles")
    @GetMapping({"", "/"})
    public List<UserProfileResponse> getAllUserProfiles() {
        return service.getAllUsers();
    }

    @Operation(summary = "Create a demo profile")
    @PostMapping
    public ResponseEntity<UserProfileResponse> createUserProfile(@Valid @RequestBody UserProfileRequest request) {
        var created = service.createUserProfile(request);
        return ResponseEntity.created(URI.create("/api/v1/demo/users/" + created.username())).body(created);
    }

    @Operation(summary = "Replace one profile field")
    @PatchMapping("/{username}")
    public UserProfileResponse updateUserProfile(
            @PathVariable @Pattern(regexp = "[A-Za-z0-9]{1,32}") String username,
            @Valid @RequestBody UpdateUserRequest request) {
        return service.updateUserProfile(username, request);
    }

    @Operation(summary = "Delete a demo profile")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserProfile(
            @PathVariable @Pattern(regexp = "[A-Za-z0-9]{1,32}") String username) {
        service.deleteUserProfile(username);
        return ResponseEntity.noContent().build();
    }
}
