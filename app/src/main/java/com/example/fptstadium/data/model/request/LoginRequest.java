package com.example.fptstadium.data.model.request;

public class LoginRequest {
    private String email;
    private String password;
    private boolean rememberMe;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.rememberMe = false;
    }

    public LoginRequest(String email, String password, boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }
}
