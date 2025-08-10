package com.example.DemoApp.controller;

import com.example.DemoApp.service.UserProfileService;
import com.example.DemoApp.validation.UserProfileValidator;
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

    // GET route fetches a single user by username
    @Operation(summary = "Get user by username", description = "Returns a user profile for the given username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid Username"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "Username", example = "testuser123")
            @PathVariable String username) {
        try {
            UserProfileValidator.validateUsername(username);
            return userProfileService.getUserProfile(username);
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
            summary = "Update user fields",
            description = "Updates one or more fields in the user profile using a PATCH operation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Field updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid field or value"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/users/{username}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String username, @RequestBody Map<String,String> request) {
        try {
            UserProfileValidator.validatePatchRequest(request);
            return userProfileService.updateUserProfile(username, request);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // DELETE route deletes a user profile record
    @Operation(
            summary = "Delete user",
            description = "Deletes the user profile with the given username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUserProfile(@PathVariable String username) {
        try {
            UserProfileValidator.validateUsername(username);
            return userProfileService.deleteUserProfile(username);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
