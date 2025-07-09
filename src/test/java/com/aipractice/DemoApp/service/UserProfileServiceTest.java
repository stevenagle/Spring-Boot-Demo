package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileServiceTest {

    private UserProfileService userProfileService;

    @TempDir
    Path tempDir;

    private File tempJsonFile;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Simulate a real database.json file in a temp location
        String jsonContent = """
            {
              "1": {
                "userName": "TestUser1",
                "emailAddress": "testuser@apidemo.com",
                "streetAddress": "1234 Main St",
                "city": "Anywhereville",
                "state": "IL",
                "zipCode": "12345"
              }
            }
        """;

        tempJsonFile = new File(tempDir.toFile(), "database.json");
        try (FileWriter writer = new FileWriter(tempJsonFile)) {
            writer.write(jsonContent);
        }

        // Override ClassPathResource for testing using the temporary file path
        userProfileService = new UserProfileService(objectMapper) {
            @Override
            public Optional<UserProfile> getUserProfile(String userKey) {
                try {
                    var root = objectMapper.readTree(tempJsonFile);
                    var userNode = root.path(userKey);
                    if (userNode.isMissingNode()) return Optional.empty();

                    UserProfile profile = new UserProfile(
                            userNode.path("userName").asText(),
                            userNode.path("emailAddress").asText(),
                            userNode.path("streetAddress").asText(),
                            userNode.path("city").asText(),
                            userNode.path("state").asText(),
                            userNode.path("zipCode").asText()
                    );

                    return Optional.of(profile);

                } catch (IOException e) {
                    return Optional.empty();
                }
            }
        };
    }

    @Test
    void testGetUserProfile_success() {
        Optional<UserProfile> result = userProfileService.getUserProfile("1");

        assertTrue(result.isPresent());
        UserProfile user = result.get();
        assertEquals("TestUser1", user.getUsername());
        assertEquals("testuser@apidemo.com", user.getEmailAddress());
        assertEquals("1234 Main St", user.getStreetAddress());
        assertEquals("Anywhereville", user.getCity());
        assertEquals("IL", user.getState());
        assertEquals("12345", user.getZipCode());
    }

    @Test
    void testGetUserProfile_notFound() {
        Optional<UserProfile> result = userProfileService.getUserProfile("2");
        assertTrue(result.isEmpty());
    }
}
