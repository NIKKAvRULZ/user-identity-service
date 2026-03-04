package com.foodsystem.user_identity_service.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Default constructor for JSON mapping
    public LoginRequest() {}

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters (Crucial for Spring to populate the object)
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}