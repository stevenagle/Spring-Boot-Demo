package com.example.DemoApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** A single replacement operation; only explicitly supported profile fields are writable. */
public record UpdateUserRequest(
        @NotBlank @Pattern(regexp = "replace") String op,
        @NotBlank @Pattern(regexp = "username|emailAddress|streetAddress|city|state|zipCode") String path,
        @NotBlank @Size(max = 255) String value) {
}
