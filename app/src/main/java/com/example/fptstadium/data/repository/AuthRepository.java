package com.example.fptstadium.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fptstadium.api.AuthService;
import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.request.RegisterRequest;
import com.example.fptstadium.data.model.response.LoginResponse;
import com.example.fptstadium.data.model.response.RegisterResponse;
import com.example.fptstadium.utils.PrefsHelper;

import javax.inject.Inject;

import dagger.hilt.InstallIn;

public class AuthRepository {
    private final AuthService authService;
    private final PrefsHelper prefsHelper;

    @Inject
    public AuthRepository(AuthService authService, PrefsHelper prefsHelper) {
        this.authService = authService;
        this.prefsHelper = prefsHelper;
    }

    public LiveData<LoginResponse> login(String email, String password, boolean rememberMe) {
        MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

        authService.login(new LoginRequest(email, password, rememberMe))
                .enqueue(new retrofit2.Callback<LoginResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            if (loginResponse.isSuccess() && loginResponse.getData() != null) {
                                String accessToken = loginResponse.getData().getAccessToken();
                                String refreshToken = loginResponse.getData().getRefreshToken();
                                long accessTokenExpiresAt = loginResponse.getData().getAccessTokenExpiresAt();
                                long refreshTokenExpiresAt = loginResponse.getData().getRefreshTokenExpiresAt();
                                
                                prefsHelper.saveAccessToken(accessToken);
                                prefsHelper.saveRefreshToken(refreshToken);
                                prefsHelper.saveAccessTokenExpiresAt(accessTokenExpiresAt);
                                prefsHelper.saveRefreshTokenExpiresAt(refreshTokenExpiresAt);
                                
                                loginResult.setValue(loginResponse);
                            } else {
                                loginResult.setValue(null);
                            }
                        } else {
                            loginResult.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                        loginResult.setValue(null);
                    }
                });
        return loginResult;
    }

    // Overloaded method for backward compatibility
    public LiveData<LoginResponse> login(String email, String password) {
        return login(email, password, false);
    }

    public LiveData<RegisterResponse> register(String email, String password, String confirmPassword,
                                                String firstName, String lastName, String phoneNumber) {
        MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();

        authService.register(new RegisterRequest(email, password, confirmPassword, firstName, lastName, phoneNumber))
                .enqueue(new retrofit2.Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RegisterResponse registerResponse = response.body();
                            registerResult.setValue(registerResponse);
                        } else {
                            registerResult.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<RegisterResponse> call, Throwable t) {
                        registerResult.setValue(null);
                    }
                });
        return registerResult;
    }
}
