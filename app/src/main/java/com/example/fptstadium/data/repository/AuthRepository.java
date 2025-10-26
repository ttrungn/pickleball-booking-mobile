package com.example.fptstadium.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fptstadium.api.AuthService;
import com.example.fptstadium.data.model.request.LoginRequest;
import com.example.fptstadium.data.model.response.LoginResponse;
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

    public LiveData<LoginResponse> login(String username, String password) {
        MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

        authService.login(new LoginRequest(username, password))
                .enqueue(new retrofit2.Callback<LoginResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getData().getToken();
                            prefsHelper.saveToken(token);
                            loginResult.setValue(response.body());
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
}
