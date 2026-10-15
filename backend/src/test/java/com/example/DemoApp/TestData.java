package com.example.DemoApp;

import com.example.DemoApp.domain.UserProfile;
import com.example.DemoApp.dto.UserProfileRequest;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;


/** Creates fresh test fixtures without coupling tests to seed IDs or shared mutable entities. */
public final class TestData {
    private TestData() { }

    public static UserProfileRequest request(String username) {
        return new UserProfileRequest(username, "test@example.com", "123 Main St", "Chicago", "IL", "60601");
    }

    public static UserProfile user(String username) {
        return UserProfile.builder().username(username).emailAddress("test@example.com")
                .streetAddress("123 Main St").city("Chicago").state("IL").zipCode("60601").build();
    }

    public static UserProfileRequest requestWith(String field, String value) {
        var base = request("sample");
        return new UserProfileRequest(
                field.equals("username") ? value : base.username(),
                field.equals("emailAddress") ? value : base.emailAddress(),
                field.equals("streetAddress") ? value : base.streetAddress(),
                field.equals("city") ? value : base.city(),
                field.equals("state") ? value : base.state(),
                field.equals("zipCode") ? value : base.zipCode());
    }

    public static Stream<Arguments> invalidProfileFields() {
        var required = Stream.of("username", "emailAddress", "streetAddress", "city", "state", "zipCode")
                .flatMap(field -> Stream.of(null, "", " ").map(value -> Arguments.of(field, value)));
        return Stream.concat(required, Stream.of(
                Arguments.of("username", "bad!"), Arguments.of("username", "a".repeat(33)),
                Arguments.of("emailAddress", "invalid"), Arguments.of("emailAddress", boundaryEmail() + "a"),
                Arguments.of("streetAddress", "a".repeat(256)), Arguments.of("city", "a".repeat(101)),
                Arguments.of("state", "Illinois"), Arguments.of("state", "il"),
                Arguments.of("zipCode", "abc"), Arguments.of("zipCode", "1234"),
                Arguments.of("zipCode", "123456"), Arguments.of("zipCode", "12345-123")));
    }

    public static String boundaryEmail() {
        return "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(61);
    }

    public static List<UserProfile> provideUserProfiles() {
        return List.of(user("TestUser1"), user("TestUser2"));
    }
}
