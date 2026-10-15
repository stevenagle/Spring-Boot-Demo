package com.example.DemoApp.integration.service;

import com.example.DemoApp.TestData;
import com.example.DemoApp.dto.UpdateUserRequest;
import com.example.DemoApp.repository.UserProfileRepository;
import com.example.DemoApp.service.UserProfileService;
import com.example.DemoApp.validation.UserProfileValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import static org.assertj.core.api.Assertions.*;

/** Verifies service transactions against the real JPA repository. */
@DataJpaTest
@Import({UserProfileService.class, UserProfileValidator.class, ValidationAutoConfiguration.class})
class UserProfileServiceIntegrationTest {
    @Autowired UserProfileService service;
    @Autowired UserProfileRepository repository;

    @Test void createsRenamesAndDeletesProfile() {
        var created = service.createUserProfile(TestData.request("serviceTest"));
        var updated = service.updateUserProfile("serviceTest", new UpdateUserRequest("replace", "username", "renamed"));
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(repository.findByUsername("serviceTest")).isEmpty();
        assertThat(service.getUserProfile("renamed").username()).isEqualTo("renamed");
        service.deleteUserProfile("renamed");
        repository.flush();
        assertThat(repository.findByUsername("renamed")).isEmpty();
    }
}
