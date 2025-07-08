package com.aipractice.DemoApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DemoController.class)
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetResource() throws Exception {
        mockMvc.perform(get("/api/v1/demo"))
                .andExpect(status().isOk())
                .andExpect(content().string("Resource data retrieved successfully."));
    }

    @Test
    void testCreateResource() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "TestUser");

        mockMvc.perform(post("/api/v1/demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Resource 'TestUser' created successfully."));
    }
}
