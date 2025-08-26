package com.example.saferide;

public class User {
    public String userId;
    public String email;
    public String deviceCode;

    // Default constructor required for Firebase
    public User() {
    }

    // Constructor to initialize values
    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
