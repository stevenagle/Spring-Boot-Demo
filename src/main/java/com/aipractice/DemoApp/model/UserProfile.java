package com.aipractice.DemoApp.model;

public class UserProfile {

    private String username;
    private Integer userId;
    private String emailAddress;

    // Constructors
    public UserProfile() {}

    public UserProfile(String username, Integer userId, String emailAddress) {
        this.username = username;
        this.userId = userId;
        this.emailAddress = emailAddress;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    // Optionally override toString() for logging/debugging
    @Override
    public String toString() {
        return "UserProfile{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
