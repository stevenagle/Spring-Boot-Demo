package com.aipractice.DemoApp.dto;

import com.aipractice.DemoApp.validation.ValidPatchPath;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserProfilePatchDTO(
        @NotBlank(message = "Operation type is required.")
        @Pattern(regexp = "replace", message = "Only 'replace' is supported.")
        String op,

        @ValidPatchPath
        @NotBlank(message = "Field path is required.")
        String path,

        @NotBlank(message = "Field value is required.")
        String value
) {}
