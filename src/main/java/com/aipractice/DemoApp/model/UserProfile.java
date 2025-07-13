package com.aipractice.DemoApp.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    private String username;
    private String emailAddress;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    @Override
    public String toString() {
        return "UserProfile{" +
                "username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }


}
