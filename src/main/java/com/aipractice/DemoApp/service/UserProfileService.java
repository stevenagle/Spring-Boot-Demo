package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.domain.UserProfile;
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

            UserProfile profile = UserProfile.builder()
                    .id(1L)
                    .username(userNode.path("userName").asText())
                    .emailAddress(userNode.path("emailAddress").asText())
                    .streetAddress(userNode.path("streetAddress").asText())
                    .city(userNode.path("city").asText())
                    .state(userNode.path("state").asText())
                    .zipCode(userNode.path("zipCode").asText())
                    .build();

            return Optional.of(profile);

        } catch (IOException e) {
            e.printStackTrace(); // Consider logging in production
            return Optional.empty();
        }
    }
}


