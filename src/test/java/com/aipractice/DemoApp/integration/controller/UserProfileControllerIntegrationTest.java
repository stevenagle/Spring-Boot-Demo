package com.aipractice.DemoApp.integration.controller;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new UserProfile(null, "alpha001", "alpha@example.com", "123 Main St", "Chicago", "IL", "60601"),
                new UserProfile(null, "bravo002", "bravo@example.com", "456 Oak Ave", "Springfield", "IL", "62704")
        ));
    }

    // GET tests
    @Test
    void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/demo/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].username", is("alpha001")));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        Long id = repository.findAll().get(0).getId();

        mockMvc.perform(get("/api/v1/demo/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress", is("alpha@example.com")));
    }

    @Test
    void getUser_invalidIdFormat_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/demo/users/banana"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID should be numeric and less than 12 digits.")));
    }

    @Test
    void getUser_nonExistentId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/demo/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("does not exist")));
    }

    // POST tests
    @Test
    void createUser_validPayload_shouldSucceed() throws Exception {
        String json = """
            {
              "username": "charlie003",
              "emailAddress": "charlie@example.com",
              "streetAddress": "789 Pine Rd",
              "city": "Naperville",
              "state": "IL",
              "zipCode": "60540"
            }
            """;

        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("created successfully")));
    }

    @Test
    void createUser_missingUsername_shouldReturn400() throws Exception {
        String json = """
            {
              "emailAddress": "missing@example.com",
              "streetAddress": "123 Missing St",
              "city": "Nowhere",
              "state": "IL",
              "zipCode": "00000"
            }
            """;

        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing required field: username")));
    }

    @Test
    void createUser_invalidEmail_shouldTriggerValidator() throws Exception {
        String json = """
            {
              "username": "bademail",
              "emailAddress": "not-an-email",
              "streetAddress": "123 Fake St",
              "city": "Faketown",
              "state": "IL",
              "zipCode": "12345"
            }
            """;

        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));
    }

    // PATCH tests
    @Test
    void updateUser_validPatch_shouldSucceed() throws Exception {
        Long id = repository.findAll().get(0).getId();

        String patchJson = """
            {
              "op": "replace",
              "path": "city",
              "value": "UpdatedCity"
            }
            """;

        mockMvc.perform(patch("/api/v1/demo/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("city updated")));
    }

    @Test
    void updateUser_invalidField_shouldReturn400() throws Exception {
        Long id = repository.findAll().get(0).getId();

        String patchJson = """
            {
              "op": "replace",
              "path": "unknownField",
              "value": "Oops"
            }
            """;

        mockMvc.perform(patch("/api/v1/demo/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("cannot be updated")));
    }

    @Test
    void updateUser_nonExistentId_shouldReturn404() throws Exception {
        String patchJson = """
            {
              "op": "replace",
              "path": "state",
              "value": "NE"
            }
            """;

        mockMvc.perform(patch("/api/v1/demo/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("That user does not exist. Please try again.")));
    }

    // DELETE tests
    @Test
    void deleteUser_validId_shouldReturn204() throws Exception {
        Long id = repository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/v1/demo/users/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_invalidIdFormat_shouldReturn400() throws Exception {
        mockMvc.perform(delete("/api/v1/demo/users/abc123"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID should be numeric and less than 12 digits.")));
    }

    @Test
    void deleteUser_nonExistentId_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/v1/demo/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("does not exist")));
    }
}
