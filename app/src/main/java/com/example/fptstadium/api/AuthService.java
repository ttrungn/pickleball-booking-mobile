package com.example.fptstadium.api;

import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.request.RegisterRequest;
import com.example.fptstadium.data.model.response.LoginResponse;
import com.example.fptstadium.data.model.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/customer/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
}
