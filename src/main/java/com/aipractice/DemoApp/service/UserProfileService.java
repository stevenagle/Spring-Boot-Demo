package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.model.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserProfileService {

    private final ObjectMapper objectMapper;

    public UserProfileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<UserProfile> getUserProfile(String userKey) {
        try {
            // Load the JSON file from classpath
            JsonNode root = objectMapper.readTree(new ClassPathResource("database.json").getInputStream());

            // Get the specified user node
            JsonNode userNode = root.path(userKey);

            if (userNode.isMissingNode()) {
                return Optional.empty(); // User not found
            }

            // Build the POJO manually
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
            e.printStackTrace(); // Consider logging in production
            return Optional.empty();
        }
    }
}


