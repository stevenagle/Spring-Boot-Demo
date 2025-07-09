package com.aipractice.DemoApp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfile {

    private String username;
    private String emailAddress;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    public UserProfile(String username, String emailAddress, String streetAddress, String city, String state, String zipCode) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

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
