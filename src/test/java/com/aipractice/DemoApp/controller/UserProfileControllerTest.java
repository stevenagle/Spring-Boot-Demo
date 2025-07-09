package com.aipractice.DemoApp.controller;

import com.aipractice.DemoApp.service.UserProfileService;
import com.aipractice.DemoApp.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Test
    void testGetUserProfileSuccess_id1() throws Exception {
        UserProfile user = new UserProfile(
                "TestUser1",
                "testuser@apidemo.com",
                "1234 Main St",
                "Anywhereville",
                "IL",
                "12345"
        );
        when(userProfileService.getUserProfile("1")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/demo/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("TestUser1"))
                .andExpect(jsonPath("$.emailAddress").value("testuser@apidemo.com"))
                .andExpect(jsonPath("$.streetAddress").value("1234 Main St"))
                .andExpect(jsonPath("$.city").value("Anywhereville"))
                .andExpect(jsonPath("$.state").value("IL"))
                .andExpect(jsonPath("$.zipCode").value("12345"));
    }

    @Test
    void testGetUserProfileSuccess_id2() throws Exception {
        UserProfile user = new UserProfile(
                "TestUser2",
                "testuser2@apidemo.com",
                "5678 Main St",
                "Somewheretown",
                "TX",
                "67890"
        );
        when(userProfileService.getUserProfile("2")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/demo/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("TestUser2"))
                .andExpect(jsonPath("$.emailAddress").value("testuser2@apidemo.com"))
                .andExpect(jsonPath("$.streetAddress").value("5678 Main St"))
                .andExpect(jsonPath("$.city").value("Somewheretown"))
                .andExpect(jsonPath("$.state").value("TX"))
                .andExpect(jsonPath("$.zipCode").value("67890"));
    }

    @Test
    void testGetUserProfileNotFound_id3() throws Exception {
        when(userProfileService.getUserProfile("3")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/demo/user/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("That user does not exist. Please try again."));
    }

    @Test
    void testGetUserProfileInvalidWordId() throws Exception {
        when(userProfileService.getUserProfile("banana")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/demo/user/banana"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only numbers are supported for user lookup."));
    }
}
