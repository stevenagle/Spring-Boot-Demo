package com.example.DemoApp.unit.service;

import com.example.DemoApp.service.UserProfileService;
import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.exception.InvalidUpdateException;
import com.example.DemoApp.exception.UserAlreadyExistsException;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository repo;

    @InjectMocks
    private UserProfileService service;

    private UserProfile sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserProfile.builder()
                .id(123L)
                .username("alice")
                .emailAddress("alice@example.com")
                .streetAddress("123 Main St")
                .city("Wonderland")
                .state("IL")
                .zipCode("60601")
                .build();
    }

    //
    // getUserProfile(...)
    //

    @Test
    void getUserProfile_whenFound_returns200AndBody() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));

        ResponseEntity<?> resp = service.getUserProfile("alice");

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isSameAs(sampleUser);
    }

    @Test
    void getUserProfile_whenNotFound_returns404AndMessage() {
        when(repo.findByUsername("bob")).thenReturn(Optional.empty());

        ResponseEntity<?> resp = service.getUserProfile("bob");

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isEqualTo("That user does not exist. Please try again.");
    }

    @Test
    void getUserProfile_onGenericException_returns500() {
        when(repo.findByUsername(anyString()))
                .thenThrow(new RuntimeException("oops"));

        ResponseEntity<?> resp = service.getUserProfile("anything");

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody())
                .isEqualTo("A server error occurred while retrieving the user profile.");
    }

    //
    // getAllUsers()
    //

    @Test
    void getAllUsers_whenEmpty_returns200AndMsg() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> resp = service.getAllUsers();

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("No user profiles found in the database.");
    }

    @Test
    void getAllUsers_whenNonEmpty_returns200AndList() {
        List<UserProfile> list = List.of(sampleUser);
        when(repo.findAll()).thenReturn(list);

        ResponseEntity<?> resp = service.getAllUsers();

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isSameAs(list);
    }

    @Test
    void getAllUsers_onException_returns500() {
        when(repo.findAll()).thenThrow(new RuntimeException("fail"));

        ResponseEntity<?> resp = service.getAllUsers();

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody())
                .isEqualTo("A server error occurred while retrieving user profiles.");
    }

    //
    // createUserProfile(...)
    //

    @Test
    void createUserProfile_success_returns200WithIdMsg() {
        Map<String,String> payload = Map.of(
                "username",      sampleUser.getUsername(),
                "emailAddress",  sampleUser.getEmailAddress(),
                "streetAddress", sampleUser.getStreetAddress(),
                "city",          sampleUser.getCity(),
                "state",         sampleUser.getState(),
                "zipCode",       sampleUser.getZipCode()
        );

        when(repo.save(any())).thenReturn(sampleUser);

        ResponseEntity<String> resp = service.createUserProfile(payload);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody())
                .isEqualTo("New user ID " + sampleUser.getId() + " created successfully.");
    }

    @Test
    void createUserProfile_whenDuplicate_throwsUserAlreadyExists() {
        when(repo.save(any()))
                .thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> service.createUserProfile(Map.of()))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void createUserProfile_onGenericException_returns500() {
        when(repo.save(any()))
                .thenThrow(new RuntimeException("oops"));

        ResponseEntity<String> resp = service.createUserProfile(Map.of());

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody())
                .isEqualTo("An error occurred while creating a user profile. Double-check your request.");
    }

    //
    // updateUserProfile(...)
    //

    @Test
    void updateUserProfile_usernameChange_succeeds() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);

        Map<String,String> payload = Map.of("path","username", "value","bob");
        ResponseEntity<?> resp = service.updateUserProfile("alice", payload);

        verify(repo).saveAndFlush(captor.capture());
        UserProfile saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("bob");

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isEqualTo("username updated for user: bob");
    }

    @Test
    void updateUserProfile_nonexistent_throwsUserNotFound() {
        when(repo.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateUserProfile("missing", Map.of("path","city","value","X"))
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUserProfile_invalidField_throwsInvalidUpdate() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));

        assertThatThrownBy(() ->
                service.updateUserProfile("alice", Map.of("path","nope","value","X"))
        ).isInstanceOf(InvalidUpdateException.class);
    }

    @Test
    void updateUserProfile_duplicateUsername_throwsUserAlreadyExists() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));
        doThrow(new DataIntegrityViolationException("dup"))
                .when(repo).saveAndFlush(any());

        assertThatThrownBy(() ->
                service.updateUserProfile("alice", Map.of("path","username","value","carol"))
        ).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void updateUserProfile_onGenericException_returns500() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));
        doThrow(new RuntimeException("fail"))
                .when(repo).saveAndFlush(any());

        ResponseEntity<?> resp = service.updateUserProfile("alice",
                Map.of("path","city","value","X"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody())
                .isEqualTo("Server error occurred while updating user profile.");
    }

    //
    // deleteUserProfile(...)
    //

    @Test
    void deleteUserProfile_exists_returns204() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));

        ResponseEntity<?> resp = service.deleteUserProfile("alice");

        verify(repo).delete(sampleUser);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resp.getBody()).isNull();
    }

    @Test
    void deleteUserProfile_missing_throwsUserNotFound() {
        when(repo.findByUsername("alice")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUserProfile("alice"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUserProfile_onGenericException_returns500() {
        when(repo.findByUsername("alice")).thenReturn(Optional.of(sampleUser));
        doThrow(new RuntimeException("boom"))
                .when(repo).delete(sampleUser);

        ResponseEntity<?> resp = service.deleteUserProfile("alice");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody())
                .isEqualTo("Server error occurred while deleting user profile.");
    }
}
