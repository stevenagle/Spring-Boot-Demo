package com.example.DemoApp.unit.service;

import com.example.DemoApp.TestData;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.dto.UserProfileResponse;
import com.example.DemoApp.exception.UserAlreadyExistsException;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.repository.UserProfileRepository;
import com.example.DemoApp.service.UserProfileService;
import com.example.DemoApp.validation.UserProfileValidator;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Verifies service decisions and validation independently of HTTP and persistence. */
@ExtendWith(MockitoExtension.class)
class UserProfileServiceUnitTest {
    @Mock UserProfileRepository repository;
    static ValidatorFactory factory;
    UserProfileService service;

    @BeforeAll static void openValidator() { factory = Validation.buildDefaultValidatorFactory(); }
    @AfterAll static void closeValidator() { factory.close(); }
    @BeforeEach void setUp() { service = new UserProfileService(repository, new UserProfileValidator(factory.getValidator())); }

    @ParameterizedTest @ValueSource(strings = {"get", "update", "delete"})
    void missingUserThrowsDomainException(String operation) {
        when(repository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> invoke(operation, "missing")).isInstanceOf(UserNotFoundException.class);
        verify(repository, never()).saveAndFlush(any());
        verify(repository, never()).delete(any(com.example.DemoApp.domain.UserProfile.class));
    }

    @Test void invalidUpdateDoesNotMutateEntityOrPersist() {
        var user = TestData.user("sample");
        when(repository.findByUsername("sample")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> service.updateUserProfile("sample", new UpdateUserRequest("replace", "emailAddress", "invalid")))
                .isInstanceOf(ConstraintViolationException.class);
        assertThat(user.getEmailAddress()).isEqualTo("test@example.com");
        verify(repository, never()).saveAndFlush(any());
    }

    @ParameterizedTest @ValueSource(strings = {"create", "update"})
    void duplicateWriteBecomesDomainConflict(String operation) {
        if (operation.equals("update")) when(repository.findByUsername("sample")).thenReturn(Optional.of(TestData.user("sample")));
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("unique constraint"));
        assertThatThrownBy(() -> invoke(operation, "sample"))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @ParameterizedTest @ValueSource(strings = {"get", "list", "create", "update", "delete"})
    void unexpectedRepositoryFailuresAreNotSwallowed(String operation) {
        var failure = new IllegalStateException("database unavailable");
        switch (operation) {
            case "get" -> when(repository.findByUsername("sample")).thenThrow(failure);
            case "list" -> when(repository.findAll(any(Sort.class))).thenThrow(failure);
            case "create" -> when(repository.saveAndFlush(any())).thenThrow(failure);
            case "update", "delete" -> {
                when(repository.findByUsername("sample")).thenReturn(Optional.of(TestData.user("sample")));
                if (operation.equals("update")) when(repository.saveAndFlush(any())).thenThrow(failure);
                else doThrow(failure).when(repository).delete(any(com.example.DemoApp.domain.UserProfile.class));
            }
            default -> throw new AssertionError("Unexpected test case");
        }
        assertThatThrownBy(() -> invoke(operation, "sample")).isSameAs(failure);
    }

    @ParameterizedTest
    @MethodSource("com.example.DemoApp.TestData#invalidProfileFields")
    void invalidCreateNeverReachesRepository(String field, String value) {
        assertThatThrownBy(() -> service.createUserProfile(TestData.requestWith(field, value)))
                .isInstanceOf(ConstraintViolationException.class);
        verifyNoInteractions(repository);
    }

    @Test void returnsCompleteProfilesInRepositoryOrder() {
        var alpha = TestData.user("alpha");
        var zulu = TestData.user("zulu");
        when(repository.findAll(Sort.by("username"))).thenReturn(List.of(alpha, zulu));
        assertThat(service.getAllUsers()).containsExactly(UserProfileResponse.from(alpha), UserProfileResponse.from(zulu));
    }

    private void invoke(String operation, String username) {
        switch (operation) {
            case "get" -> service.getUserProfile(username);
            case "list" -> service.getAllUsers();
            case "create" -> service.createUserProfile(TestData.request(username));
            case "update" -> service.updateUserProfile(username, new UpdateUserRequest("replace", "username", "renamed"));
            case "delete" -> service.deleteUserProfile(username);
            default -> throw new AssertionError("Unexpected test case");
        }
    }

    @Test void deleteUsesTheResolvedEntity() {
        var user = TestData.user("sample");
        when(repository.findByUsername("sample")).thenReturn(Optional.of(user));
        service.deleteUserProfile("sample");
        verify(repository).delete(user);
    }
}
