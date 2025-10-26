package com.example.fptstadium.api;

import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("accounts/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
