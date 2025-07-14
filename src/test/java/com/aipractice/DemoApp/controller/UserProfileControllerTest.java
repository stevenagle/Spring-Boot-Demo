package com.aipractice.DemoApp.controller;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    private static final String VALID_USER_JSON = """
        {
          "username": "TestUser3",
          "emailAddress": "testuser3@apidemo.com",
          "streetAddress": "7890 Elm St",
          "city": "Newtown",
          "state": "CA",
          "zipCode": "90210"
        }
        """;

    @Test
    void testGetUserProfileSuccess_id1() throws Exception {
        var user = UserProfile.builder()
                .id(1L)
                .username("TestUser1")
                .emailAddress("testuser@apidemo.com")
                .streetAddress("1234 Main St")
                .city("Anywhereville")
                .state("IL")
                .zipCode("12345")
                .build();

        when(userProfileService.getUserProfile("1"))
                .thenAnswer(x -> ResponseEntity.status(HttpStatus.OK).body(user));

        mockMvc.perform(get("/api/v1/demo/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("TestUser1"))
                .andExpect(jsonPath("$.emailAddress").value("testuser@apidemo.com"))
                .andExpect(jsonPath("$.streetAddress").value("1234 Main St"))
                .andExpect(jsonPath("$.city").value("Anywhereville"))
                .andExpect(jsonPath("$.state").value("IL"))
                .andExpect(jsonPath("$.zipCode").value("12345"));
    }

    @Test
    void testGetUserProfileNotFound_id3() throws Exception {
        when(userProfileService.getUserProfile("3"))
                .thenAnswer(x -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("That user does not exist. Please try again."));

        mockMvc.perform(get("/api/v1/demo/users/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("That user does not exist. Please try again."));
    }

    @Test
    void testGetUserProfileInvalidIdFormat() throws Exception {
        when(userProfileService.getUserProfile("banana"))
                .thenAnswer(x -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Only numbers are supported for user lookup & should be less than 12 digits."));

        mockMvc.perform(get("/api/v1/demo/users/banana"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only numbers are supported for user lookup & should be less than 12 digits."));
    }

    @Test
    void testCreateUserProfile_success() throws Exception {
        when(userProfileService.createUserProfile(Map.of(
                "username", "TestUser3",
                "emailAddress", "testuser3@apidemo.com",
                "streetAddress", "7890 Elm St",
                "city", "Newtown",
                "state", "CA",
                "zipCode", "90210"
        ))).thenReturn(ResponseEntity.ok("New user ID 11 created successfully."));

        mockMvc.perform(post(URI.create("/api/v1/demo/users"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("New user ID 11 created successfully."));
    }

    @Test
    void testCreateUserProfile_missingUserName() throws Exception {
        String json = VALID_USER_JSON.replace("\"username\": \"TestUser3\",", "");
        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Missing required field: username"));
    }
}
