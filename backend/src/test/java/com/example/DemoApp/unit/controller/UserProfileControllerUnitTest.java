package com.example.DemoApp.unit.controller;

import com.example.DemoApp.TestData;
import com.example.DemoApp.controller.UserProfileController;
import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerUnitTest {

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

    private static final String VALID_PATCH_BODY_ADDRESS_JSON = """
            {
              "op": "replace",
              "path": "streetAddress",
              "value": "0987 Main St"
            }
            """;

    private static final String VALID_PATCH_BODY_CITY_JSON = """
            {
              "op": "replace",
              "path": "state",
              "value": "NE"
            }
            """;

    private static final String INVALID_PATCH_BODY = """
            {
              "op": "replace",
              "path": "fakeField",
              "value": "oops"
            }
            """;


    // GET tests
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
                .andExpect(content().string("User ID should be numeric and less than 12 digits."));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        List<UserProfile> mockUsers = TestData.provideUserProfiles();

        when(userProfileService.getAllUsers()).thenAnswer(x -> ResponseEntity.ok(mockUsers));

        mockMvc.perform(get("/api/v1/demo/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockUsers.size()))
                .andExpect(jsonPath("$[0].username").value("TestUser1"))
                .andExpect(jsonPath("$[1].emailAddress").value("testuser2@apidemo.com"))
                .andExpect(jsonPath("$[2].city").value("Naperville"));
    }

    // POST tests

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

    // PATCH tests

    @Test
    void testUpdateUserProfile_shouldReturn200WhenSuccessful() throws Exception {
        Map<String, String> updatePayload = Map.of(
                "op", "replace",
                "path", "streetAddress",
                "value", "0987 Main St"
        );

        when(userProfileService.updateUserProfile("10", updatePayload))
                .thenAnswer(x -> ResponseEntity.ok("streetAddress updated for user ID 10"));

        mockMvc.perform(patch("/api/v1/demo/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PATCH_BODY_ADDRESS_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("streetAddress updated for user ID 10"));
    }

    @Test
    void testUpdateUserProfile_userNotFound_shouldReturn404() throws Exception {
        Map<String, String> updatePayload = Map.of(
                "op", "replace",
                "path", "state",
                "value", "NE"
        );

        when(userProfileService.updateUserProfile("999", updatePayload))
                .thenAnswer(x -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No user found with ID 999"));

        mockMvc.perform(patch("/api/v1/demo/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PATCH_BODY_CITY_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No user found with ID 999"));
    }

    @Test
    void testUpdateUserProfile_invalidField_shouldReturn400() throws Exception {
        Map<String, String> updatePayload = Map.of(
                "op", "replace",
                "path", "fakeField",
                "value", "oops"
        );

        when(userProfileService.updateUserProfile("1", updatePayload))
                .thenAnswer(x -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Field 'fakeField' cannot be updated."));

        mockMvc.perform(patch("/api/v1/demo/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_PATCH_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field 'fakeField' cannot be updated. Allowed fields: username, emailAddress, streetAddress, city, state, zipCode"));
    }

    // DELETE tests

    @Test
    void testDeleteUserProfile_shouldReturn204WhenSuccessful() throws Exception {
        when(userProfileService.deleteUserProfile("1"))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/v1/demo/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserProfile_userNotFound_shouldReturn404() throws Exception {
        when(userProfileService.deleteUserProfile("999"))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(delete("/api/v1/demo/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("That user does not exist. Please try again."));
    }

    @Test
    void testDeleteUserProfile_invalidIdFormat_shouldReturn400() throws Exception {
        mockMvc.perform(delete("/api/v1/demo/users/abc123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User ID should be numeric and less than 12 digits."));

    }
}