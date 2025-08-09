package com.example.DemoApp.integration.repository;

import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserProfileDatabaseTest {

    @Autowired
    private UserProfileRepository repository;

    @Test
    void testH2DatabaseSeededWithTenProfiles() {
        List<UserProfile> users = repository.findAll();
        assertEquals(10, users.size(), "Expected exactly 10 seeded user profiles.");
    }
}
