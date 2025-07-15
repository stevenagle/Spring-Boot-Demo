package com.aipractice.DemoApp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(description = "Represents a user profile stored in the database")
@Table(name = "user_profile")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Schema(description = "Unique identifier for the user", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Username for login or display", example = "TestUser1")
    @Column(nullable = false)
    private String username;

    @Schema(description = "User's email address", example = "testuser@apidemo.com")
    @Column(nullable = false)
    private String emailAddress;

    @Schema(description = "Street address of the user", example = "1234 Main St")
    @Column(nullable = false)
    private String streetAddress;

    @Schema(description = "City of residence", example = "Anywhereville")
    @Column(nullable = false)
    private String city;

    @Schema(description = "State abbreviation", example = "IL")
    @Column(nullable = false)
    private String state;

    @Schema(description = "Postal zip code", example = "12345")
    @Column(nullable = false)
    private String zipCode;
}
