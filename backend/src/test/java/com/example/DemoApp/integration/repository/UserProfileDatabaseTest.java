package com.example.DemoApp.integration.repository;

import com.example.DemoApp.TestData;
import com.example.DemoApp.repository.UserProfileRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;

/** Verifies database constraints independently of application validation. */
@DataJpaTest
class UserProfileDatabaseTest {
    @Autowired UserProfileRepository repository;
    @Autowired EntityManager entities;

    @Test void databaseRejectsDuplicateUsername() {
        repository.saveAndFlush(TestData.user("uniqueUser"));
        assertThatThrownBy(() -> repository.saveAndFlush(TestData.user("uniqueUser")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "emailAddress", "streetAddress", "city", "state", "zipCode"})
    void databaseRejectsNullRequiredFields(String field) {
        var user = TestData.user("invalidUser");
        new BeanWrapperImpl(user).setPropertyValue(field, null);
        assertThatThrownBy(() -> repository.saveAndFlush(user)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test void findsUserWithoutAssumingGeneratedId() {
        var saved = repository.saveAndFlush(TestData.user("lookupUser"));
        entities.clear();
        assertThat(repository.findByUsername("lookupUser")).hasValueSatisfying(user -> assertThat(user.getId()).isEqualTo(saved.getId()));
    }
    @ParameterizedTest
    @CsvSource({"username,33", "emailAddress,255", "streetAddress,256", "city,101", "state,3", "zipCode,11"})
    void databaseRejectsOversizedColumns(String field, int length) {
        var user = TestData.user("oversizedUser");
        new BeanWrapperImpl(user).setPropertyValue(field, "a".repeat(length));
        assertThatThrownBy(() -> repository.saveAndFlush(user)).isInstanceOf(DataIntegrityViolationException.class);
    }

}
