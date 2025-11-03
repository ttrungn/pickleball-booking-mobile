package com.example.fptstadium.api;

import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.request.RegisterRequest;
import com.example.fptstadium.data.model.request.UpdateProfileRequest;
import com.example.fptstadium.data.model.response.GetProfileResponse;
import com.example.fptstadium.data.model.response.LoginResponse;
import com.example.fptstadium.data.model.response.RegisterResponse;
import com.example.fptstadium.data.model.response.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/customer/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("users/profile")
    Call<GetProfileResponse> getProfile();

    @PUT("users/profile")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfileRequest updateProfileRequest);
}
