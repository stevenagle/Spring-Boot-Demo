package com.example.DemoApp.unit.controller;

import com.example.DemoApp.TestData;
import com.example.DemoApp.controller.UserProfileController;
import com.example.DemoApp.dto.UserProfileResponse;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Checks the HTTP boundary with a mocked application service. */
@WebMvcTest(UserProfileController.class)
class UserProfileControllerUnitTest {
    @Autowired MockMvc mvc;
    @MockitoBean UserProfileService service;

    @Test void returnsProfileRepresentation() throws Exception {
        when(service.getUserProfile("sample")).thenReturn(UserProfileResponse.from(TestData.user("sample")));
        mvc.perform(get("/api/v1/demo/users/sample")).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sample"));
    }

    @Test void mapsMissingUserToProblemDetail() throws Exception {
        when(service.getUserProfile("missing")).thenThrow(new UserNotFoundException("Profile not found."));
        mvc.perform(get("/api/v1/demo/users/missing")).andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("Profile not found."));
    }

    @ParameterizedTest
    @CsvSource({"GET,bad!", "PATCH,bad!", "DELETE,bad!", "GET,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "PATCH,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "DELETE,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void invalidUsernameNeverReachesService(String method, String username) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), "/api/v1/demo/users/" + username)
                .contentType("application/json").content("{\"op\":\"replace\",\"path\":\"city\",\"value\":\"Denver\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @ParameterizedTest
    @CsvSource({"GET,/sample", "GET,''", "POST,''", "PATCH,/sample", "DELETE,/sample"})
    void unexpectedServiceFailuresReturnSanitizedProblemDetail(String method, String suffix) throws Exception {
        var failure = new IllegalStateException("secret database credentials");
        switch (method + suffix) {
            case "GET/sample" -> when(service.getUserProfile("sample")).thenThrow(failure);
            case "GET" -> when(service.getAllUsers()).thenThrow(failure);
            case "POST" -> when(service.createUserProfile(any())).thenThrow(failure);
            case "PATCH/sample" -> when(service.updateUserProfile(eq("sample"), any())).thenThrow(failure);
            case "DELETE/sample" -> doThrow(failure).when(service).deleteUserProfile("sample");
            default -> throw new AssertionError("Unexpected test case");
        }
        String body = method.equals("POST")
                ? "{\"username\":\"sample\",\"emailAddress\":\"test@example.com\",\"streetAddress\":\"1 Main\",\"city\":\"Chicago\",\"state\":\"IL\",\"zipCode\":\"60601\"}"
                : "{\"op\":\"replace\",\"path\":\"city\",\"value\":\"Denver\"}";
        mvc.perform(request(HttpMethod.valueOf(method), "/api/v1/demo/users" + suffix)
                .contentType("application/json").content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected server error occurred. Please try again later."))
                .andExpect(content().string(not(containsString("secret database credentials"))))
                .andExpect(jsonPath("$.stacktrace").doesNotExist());
    }

    @Test void incompleteCreateIncludesFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/demo/users").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.username").exists());
        verifyNoInteractions(service);
    }
}
