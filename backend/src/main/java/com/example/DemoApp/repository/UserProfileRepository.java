package com.example.DemoApp.repository;

import com.example.DemoApp.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Provides persistence and username lookup for demo profiles. */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsername(String username);
}
