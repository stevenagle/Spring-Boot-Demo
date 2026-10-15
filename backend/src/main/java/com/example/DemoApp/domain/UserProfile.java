package com.example.DemoApp.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/** Persistence model for a demo account; API payloads are defined separately. */
@Schema(description = "Represents a user profile stored in the database")
@Table(name = "user_profile")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Schema(description = "Unique identifier for the user", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Username for login or display", example = "TestUser1")
    @Column(unique = true, nullable = false, length = 32)
    private String username;

    @Schema(description = "User's email address", example = "testuser@apidemo.com")
    @Column(nullable = false, length = 254)
    private String emailAddress;

    @Schema(description = "Street address of the user", example = "1234 Main St")
    @Column(nullable = false)
    private String streetAddress;

    @Schema(description = "City of residence", example = "Anywhereville")
    @Column(nullable = false, length = 100)
    private String city;

    @Schema(description = "State abbreviation", example = "IL")
    @Column(nullable = false, length = 2)
    private String state;

    @Schema(description = "Postal zip code", example = "12345")
    @Column(nullable = false, length = 10)
    private String zipCode;
}
