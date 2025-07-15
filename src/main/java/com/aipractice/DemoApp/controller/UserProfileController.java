package com.aipractice.DemoApp.controller;

import com.aipractice.DemoApp.service.UserProfileService;
import com.aipractice.DemoApp.validation.UserProfileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "User Profile API", description = "CRUD operations for user profiles")
@RestController
@RequestMapping("/api/v1/demo")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // GET route fetches a single user by id
    @Operation(summary = "Get user by ID", description = "Returns a user profile for the given ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid User ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "User ID (numeric only, max 12 digits)", example = "123")
            @PathVariable String id) {
        try {
            UserProfileValidator.validateUserId(id);
            return userProfileService.getUserProfile(id);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // GET ALL users
    @Operation(summary = "Get all users", description = "Returns all user profiles")
    @ApiResponse(responseCode = "200", description = "User(s) found. If empty, no users present.")
    @GetMapping("/users/")
    public ResponseEntity<?> getAllUserProfiles() {
        try {
            return userProfileService.getAllUsers();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // POST route creates a new user profile
    @Operation(
            summary = "Create a new user",
            description = "Creates a user profile with the provided fields"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid required fields")
    })
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
    @Operation(
            summary = "Update a user field",
            description = "Updates a specific field in the user profile using a PATCH operation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Field updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid field or value"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(
            summary = "Delete user",
            description = "Deletes the user profile with the given ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
