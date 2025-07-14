package com.aipractice.DemoApp.dto;

public record UserProfileResponseDTO(
        Long id,
        String username,
        String emailAddress,
        String streetAddress,
        String city,
        String state,
        String zipCode
) {}