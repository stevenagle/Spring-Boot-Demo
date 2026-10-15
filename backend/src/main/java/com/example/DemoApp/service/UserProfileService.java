package com.example.DemoApp.service;

import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.dto.UserProfileRequest;
import com.example.DemoApp.dto.UserProfileResponse;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.exception.UserAlreadyExistsException;
import com.example.DemoApp.exception.UserNotFoundException;
import com.example.DemoApp.repository.UserProfileRepository;
import com.example.DemoApp.validation.UserProfileValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Coordinates validated profile operations within transaction boundaries. */
@Service
@Transactional(readOnly = true)
public class UserProfileService {
    private final UserProfileRepository repository;
    private final UserProfileValidator validator;

    public UserProfileService(UserProfileRepository repository, UserProfileValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public UserProfileResponse getUserProfile(String username) {
        return UserProfileResponse.from(findUser(username));
    }

    public List<UserProfileResponse> getAllUsers() {
        return repository.findAll(org.springframework.data.domain.Sort.by("username")).stream()
                .map(UserProfileResponse::from).toList();
    }

    @Transactional
    public UserProfileResponse createUserProfile(UserProfileRequest request) {
        validator.validate(request);
        var user = new UserProfile();
        apply(user, request);
        return save(user);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(String username, UpdateUserRequest request) {
        validator.validate(request);
        var user = findUser(username);
        var merged = new UserProfileRequest(
                request.path().equals("username") ? request.value() : user.getUsername(),
                request.path().equals("emailAddress") ? request.value() : user.getEmailAddress(),
                request.path().equals("streetAddress") ? request.value() : user.getStreetAddress(),
                request.path().equals("city") ? request.value() : user.getCity(),
                request.path().equals("state") ? request.value() : user.getState(),
                request.path().equals("zipCode") ? request.value() : user.getZipCode());
        validator.validate(merged);
        apply(user, merged);
        return save(user);
    }

    @Transactional
    public void deleteUserProfile(String username) {
        repository.delete(findUser(username));
    }

    private UserProfile findUser(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("That user does not exist. Please try again."));
    }

    private UserProfileResponse save(UserProfile user) {
        try {
            return UserProfileResponse.from(repository.saveAndFlush(user));
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }

    private void apply(UserProfile user, UserProfileRequest request) {
        user.setUsername(request.username());
        user.setEmailAddress(request.emailAddress());
        user.setStreetAddress(request.streetAddress());
        user.setCity(request.city());
        user.setState(request.state());
        user.setZipCode(request.zipCode());
    }
}
