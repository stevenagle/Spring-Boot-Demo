package com.example.DemoApp.dto;

import com.example.DemoApp.domain.UserProfile;

/** Public account representation, independent of the persistence model. */
public record UserProfileResponse(Long id, String username, String emailAddress,
        String streetAddress, String city, String state, String zipCode) {

    public static UserProfileResponse from(UserProfile user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmailAddress(),
                user.getStreetAddress(), user.getCity(), user.getState(), user.getZipCode());
    }
}
