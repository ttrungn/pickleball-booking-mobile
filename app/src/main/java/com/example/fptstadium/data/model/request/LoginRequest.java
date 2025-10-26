package com.example.fptstadium.data.model.request;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String username, String password) {
        this.email = username;
        this.password = password;
    }
}
