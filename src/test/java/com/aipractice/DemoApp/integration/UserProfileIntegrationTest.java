package com.aipractice.DemoApp.integration;

import com.aipractice.DemoApp.domain.UserProfile;
import com.aipractice.DemoApp.repository.UserProfileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository repository;

    private UserProfile sut;
    private String getUrl;

    @BeforeEach
    void setupTestData() {
        repository.deleteAll();
        sut = repository.save(UserProfile.builder()
            .username("StartUser")
            .emailAddress("start@example.com")
            .streetAddress("100 Old St")
            .city("Oldtown")
            .state("TX")
            .zipCode("00000")
            .build());

        getUrl = "/api/v1/demo/users/" + sut.getId();
    }

    @Test
    void testGetUserProfile_shouldReturnAllFieldsCorrectly() throws Exception {
        mockMvc.perform(get(getUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sut.getId()))
                .andExpect(jsonPath("$.username").value("StartUser"))
                .andExpect(jsonPath("$.emailAddress").value("start@example.com"))
                .andExpect(jsonPath("$.streetAddress").value("100 Old St"))
                .andExpect(jsonPath("$.city").value("Oldtown"))
                .andExpect(jsonPath("$.state").value("TX"))
                .andExpect(jsonPath("$.zipCode").value("00000"));
    }

    @Test
    void testCreateUserProfile_shouldPersistNewUser() throws Exception {
        String payload = """
        {
          "username": "FreshUser",
          "emailAddress": "fresh@example.com",
          "streetAddress": "777 New Ave",
          "city": "Springfield",
          "state": "MO",
          "zipCode": "12345"
        }
        """;

        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New user ID")));

        // Verify user was persisted
        Optional<UserProfile> created = repository.findAll().stream()
                .filter(u -> u.getUsername().equals("FreshUser"))
                .findFirst();

        assertTrue(created.isPresent());
        assertEquals("fresh@example.com", created.get().getEmailAddress());
    }

    @ParameterizedTest
    @CsvSource({
            "username",
            "emailAddress",
            "streetAddress",
            "city",
            "state",
            "zipCode"
    })
    void testCreateUserProfile_missingField_shouldReturn400(String missingField) throws Exception {
        String json = """
        {
          "username": "NewUser",
          "emailAddress": "new@example.com",
          "streetAddress": "123 Test St",
          "city": "Testville",
          "state": "CA",
          "zipCode": "12345"
        }
        """;

        // Remove the target field from the JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<>() {});
        jsonMap.remove(missingField);
        String prunedJson = mapper.writeValueAsString(jsonMap);

        mockMvc.perform(post("/api/v1/demo/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prunedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Missing required field: " + missingField));
    }

    @ParameterizedTest
    @CsvSource({
            "username, NewUser",
            "emailAddress, new@example.com",
            "streetAddress, 200 New Ave",
            "city, Newville",
            "state, NY",
            "zipCode, 99999"
    })
    void testPatchUserProfile_shouldUpdateEachField(String path, String newValue) throws Exception {
        String payload = String.format("""
            {
              "op": "replace",
              "path": "%s",
              "value": "%s"
            }
            """, path, newValue);

        mockMvc.perform(patch(getUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string(path + " updated for user ID " + sut.getId()));

        mockMvc.perform(get(getUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + path).value(newValue));

        repository.saveAndFlush(sut);
    }

    @Test
    void testDeleteUserProfile_shouldRemoveRecord() throws Exception {
        mockMvc.perform(delete(getUrl))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(getUrl))
                .andExpect(status().isNotFound())
                .andExpect(content().string("That user does not exist. Please try again."));
    }
}
