package com.aipractice.DemoApp;

import com.aipractice.DemoApp.domain.UserProfile;

import java.util.List;

public class TestData {

    public static List<UserProfile> provideUserProfiles() {
        return List.of(
                new UserProfile(1L, "TestUser1", "testuser1@apidemo.com", "123 Main St", "Chicago", "IL", "60601"),
                new UserProfile(2L, "TestUser2", "testuser2@apidemo.com", "456 Oak Ave", "Springfield", "IL", "62704"),
                new UserProfile(3L, "TestUser3", "testuser3@apidemo.com", "789 Pine Rd", "Naperville", "IL", "60540"),
                new UserProfile(4L, "TestUser4", "testuser4@apidemo.com", "321 Elm St", "Peoria", "IL", "61602"),
                new UserProfile(5L, "TestUser5", "testuser5@apidemo.com", "654 Maple Blvd", "Evanston", "IL", "60201")
        );
    }
}
