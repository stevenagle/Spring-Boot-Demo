package com.example.DemoApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Validated profile input shared by account creation and merged updates. */
public record UserProfileRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9]{1,32}") String username,
        @NotBlank @Email @Size(max = 254) String emailAddress,
        @NotBlank @Size(max = 255) String streetAddress,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Pattern(regexp = "[A-Z]{2}") String state,
        @NotBlank @Pattern(regexp = "[0-9]{5}(-[0-9]{4})?") String zipCode) {
}
