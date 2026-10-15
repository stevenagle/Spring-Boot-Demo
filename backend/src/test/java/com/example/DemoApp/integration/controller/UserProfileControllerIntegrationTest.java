package com.example.DemoApp.integration.controller;

import com.example.DemoApp.TestData;
import com.example.DemoApp.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Covers malformed requests and browser access policy with the full application configuration. */
@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserProfileRepository repository;
    static final String BASE = "/api/v1/demo/users";

    @Test void accountResponsesCannotBeCachedOrSniffed() throws Exception {
        mvc.perform(get(BASE + "/alpha001")).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test void conflictingRenameRollsBackAtTheServiceBoundary() throws Exception {
        mvc.perform(patch(BASE + "/alpha001").contentType("application/json")
                .content("{\"op\":\"replace\",\"path\":\"username\",\"value\":\"bravo002\"}"))
                .andExpect(status().isConflict());
        mvc.perform(get(BASE + "/alpha001")).andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress").value("alpha001@example.com"));
        mvc.perform(get(BASE + "/bravo002")).andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAddress").value("bravo002@example.com"));
    }

    @Test void malformedJsonReturnsProblemDetail() throws Exception {
        mvc.perform(post(BASE).contentType("application/json").content("{broken"))
                .andExpect(status().isBadRequest()).andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }

    @Test void unknownFieldsAreRejected() throws Exception {
        ObjectNode payload = json.valueToTree(TestData.request("unknownFieldUser"));
        payload.put("admin", true);
        mvc.perform(post(BASE).contentType("application/json").content(json.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
        assertThat(repository.findByUsername("unknownFieldUser")).isEmpty();
    }

    @Test void allowedOriginCanPreflightPatchWithoutCredentials() throws Exception {
        mvc.perform(options(BASE + "/sample").header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "PATCH").header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }

    @Test void foreignOriginIsRejected() throws Exception {
        mvc.perform(options(BASE).header("Origin", "https://untrusted.example").header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    @Test void h2ConsoleAndDocsAreNotExposedByDefault() throws Exception {
        mvc.perform(get("/h2-console")).andExpect(status().isNotFound());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isNotFound());
    }
}
