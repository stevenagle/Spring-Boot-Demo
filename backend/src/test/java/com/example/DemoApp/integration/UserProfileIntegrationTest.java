package com.example.DemoApp.integration;

import com.example.DemoApp.TestData;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.dto.UserProfileResponse;
import com.example.DemoApp.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercises CRUD with rollback isolation and separate transactions for committed write checks. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserProfileIntegrationTest {
    static final String BASE = "/api/v1/demo/users";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserProfileRepository repository;

    @AfterEach void cleanCommittedProfiles() {
        if (TestTransaction.isActive()) return;
        for (String username : new String[]{"flowUser", "newName", "validPatch", "duplicate"}) {
            repository.findByUsername(username).ifPresent(repository::delete);
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void completeAccountLifecycle() throws Exception {
        var response = mvc.perform(post(BASE).contentType("application/json").content(json.writeValueAsString(TestData.request("flowUser"))))
                .andExpect(status().isCreated()).andExpect(header().string("Location", BASE + "/flowUser"))
                .andExpect(jsonPath("$.username").value("flowUser")).andReturn();
        var created = json.readValue(response.getResponse().getContentAsString(), UserProfileResponse.class);
        assertThat(created.id()).isPositive();
        ObjectNode expected = json.valueToTree(TestData.request("flowUser"));
        expected.put("id", created.id());
        content().json(json.writeValueAsString(expected)).match(response);
        var stored = repository.findByUsername("flowUser").orElseThrow();
        assertThat(created).isEqualTo(UserProfileResponse.from(stored));
        assertThat(stored).usingRecursiveComparison().ignoringFields("id").isEqualTo(TestData.user("flowUser"));
        mvc.perform(get(BASE + "/flowUser")).andExpect(status().isOk())
                .andExpect(content().json(json.writeValueAsString(created)));
        mvc.perform(patch(BASE + "/flowUser").contentType("application/json")
                .content(json.writeValueAsString(new UpdateUserRequest("replace", "username", "newName"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("newName"));
        mvc.perform(get(BASE + "/flowUser")).andExpect(status().isNotFound());
        expected.put("username", "newName");
        mvc.perform(get(BASE + "/newName")).andExpect(status().isOk())
                .andExpect(content().json(json.writeValueAsString(expected)));
        mvc.perform(delete(BASE + "/newName")).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get(BASE + "/newName")).andExpect(status().isNotFound());
        assertThat(repository.findByUsername("newName")).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("com.example.DemoApp.TestData#invalidProfileFields")
    void rejectsInvalidPatchWithoutChangingStoredProfile(String path, String value) throws Exception {
        var saved = repository.saveAndFlush(TestData.user("patchUser"));
        var before = UserProfileResponse.from(saved);
        mvc.perform(patch(BASE + "/patchUser").contentType("application/json")
                .content(json.writeValueAsString(new UpdateUserRequest("replace", path, value))))
                .andExpect(status().isBadRequest());
        assertThat(repository.findByUsername("patchUser")).hasValueSatisfying(user ->
                assertThat(UserProfileResponse.from(user)).isEqualTo(before));
    }

    @ParameterizedTest
    @CsvSource({"emailAddress,new@example.com", "streetAddress,456 Oak St", "city,Denver", "state,CO", "zipCode,80202-1234"})
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void persistsValidPatch(String path, String value) throws Exception {
        var saved = repository.saveAndFlush(TestData.user("validPatch"));
        ObjectNode expected = json.valueToTree(UserProfileResponse.from(saved));
        expected.put(path, value);
        mvc.perform(patch(BASE + "/validPatch").contentType("application/json")
                .content(json.writeValueAsString(new UpdateUserRequest("replace", path, value))))
                .andExpect(status().isOk()).andExpect(jsonPath("$." + path).value(value));
        mvc.perform(get(BASE + "/validPatch")).andExpect(status().isOk())
                .andExpect(content().json(json.writeValueAsString(expected)));
        assertThat(json.<ObjectNode>valueToTree(UserProfileResponse.from(repository.findByUsername("validPatch").orElseThrow())))
                .isEqualTo(expected);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void duplicateUsernameReturnsConflict() throws Exception {
        var original = UserProfileResponse.from(repository.saveAndFlush(TestData.user("duplicate")));
        long count = repository.count();
        mvc.perform(post(BASE).contentType("application/json").content(json.writeValueAsString(TestData.request("duplicate"))))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
        assertThat(repository.count()).isEqualTo(count);
        assertThat(UserProfileResponse.from(repository.findByUsername("duplicate").orElseThrow())).isEqualTo(original);
    }

    @Test void emptyCollectionIsAlwaysJsonArray() throws Exception {
        repository.deleteAll();
        mvc.perform(get(BASE)).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get(BASE + "/")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @MethodSource("com.example.DemoApp.TestData#invalidProfileFields")
    void invalidCreateReportsTheFieldAndDoesNotPersist(String field, String value) throws Exception {
        long count = repository.count();
        mvc.perform(post(BASE).contentType("application/json")
                .content(json.writeValueAsString(TestData.requestWith(field, value))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.errors." + field).exists());
        assertThat(repository.count()).isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "emailAddress", "streetAddress", "city", "state", "zipCode"})
    void missingCreateFieldReportsTheField(String field) throws Exception {
        ObjectNode payload = json.valueToTree(TestData.request("missingFieldUser"));
        payload.remove(field);
        mvc.perform(post(BASE).contentType("application/json").content(json.writeValueAsString(payload)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors." + field).exists());
        assertThat(repository.findByUsername("missingFieldUser")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PATCH", "DELETE"})
    void missingUserReturnsNotFound(String method) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), BASE + "/nonexistentUser")
                .contentType("application/json").content(json.writeValueAsString(new UpdateUserRequest("replace", "city", "Denver"))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @CsvSource({"GET,bad!", "PATCH,bad!", "DELETE,bad!", "GET,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "PATCH,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "DELETE,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"})
    void invalidPathUsernameReturnsBadRequest(String method, String username) throws Exception {
        mvc.perform(request(HttpMethod.valueOf(method), BASE + "/" + username)
                .contentType("application/json").content(json.writeValueAsString(new UpdateUserRequest("replace", "city", "Denver"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/"})
    void populatedCollectionContainsCompleteProfilesSortedByUsername(String suffix) throws Exception {
        repository.deleteAll();
        var zulu = repository.saveAndFlush(TestData.user("zulu"));
        var alpha = repository.saveAndFlush(TestData.user("alpha"));
        mvc.perform(get(BASE + suffix)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alpha"))
                .andExpect(jsonPath("$[1].username").value("zulu"))
                .andExpect(content().json(json.writeValueAsString(java.util.List.of(
                        UserProfileResponse.from(alpha), UserProfileResponse.from(zulu)))));
    }
    @ParameterizedTest
    @CsvSource({"remove,city,Denver", "replace,id,123", "replace,unknown,value", ",city,Denver", "replace,,Denver"})
    void invalidPatchOperationDoesNotChangeProfile(String op, String path, String value) throws Exception {
        var before = UserProfileResponse.from(repository.saveAndFlush(TestData.user("operationUser")));
        mvc.perform(patch(BASE + "/operationUser").contentType("application/json")
                .content(json.writeValueAsString(new UpdateUserRequest(op, path, value))))
                .andExpect(status().isBadRequest());
        assertThat(UserProfileResponse.from(repository.findByUsername("operationUser").orElseThrow())).isEqualTo(before);
    }

}
