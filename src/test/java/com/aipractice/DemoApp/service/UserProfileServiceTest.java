package com.aipractice.DemoApp.service;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.util.JsonTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileServiceTest {

    private ObjectMapper objectMapper;
    private UserProfileService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        service = new UserProfileService(objectMapper) {
            @Override
            public Optional<UserProfile> getUserProfile(String key) {
                try {
                    ObjectNode root = JsonTestUtil.readJsonFromFile("dummy_request_data.json", ObjectNode.class);
                    if (!root.has(key)) return Optional.empty();
                    return Optional.of(objectMapper.treeToValue(root.get(key), UserProfile.class));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        };
    }

    // ✅ GET tests
    @Test
    void testGetUserProfile_getTest1() {
        Optional<UserProfile> result = service.getUserProfile("getTest1");
        assertTrue(result.isPresent());
        assertEquals("AlphaUser", result.get().getUsername());
    }

    @Test
    void testGetUserProfile_getTest2() {
        Optional<UserProfile> result = service.getUserProfile("getTest2");
        assertTrue(result.isPresent());
        assertEquals("BetaUser", result.get().getUsername());
    }

    @Test
    void testGetUserProfile_getTest3() {
        Optional<UserProfile> result = service.getUserProfile("getTest3");
        assertTrue(result.isPresent());
        assertEquals("GammaUser", result.get().getUsername());
    }
}
