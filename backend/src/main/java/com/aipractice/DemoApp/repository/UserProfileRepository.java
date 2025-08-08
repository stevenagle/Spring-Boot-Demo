package com.aipractice.DemoApp.repository;

import com.aipractice.DemoApp.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
