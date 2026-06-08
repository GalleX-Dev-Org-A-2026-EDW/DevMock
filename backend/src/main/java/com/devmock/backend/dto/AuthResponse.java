package com.devmock.backend.dto;

import com.devmock.backend.entity.en_enum.UserRole;

public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private UserRole role;

    public AuthResponse(String token, String email, String fullName, UserRole role) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }
}