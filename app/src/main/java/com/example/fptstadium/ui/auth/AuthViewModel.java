package com.example.fptstadium.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.response.LoginResponse;
import com.example.fptstadium.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<LoginResponse> login(String username, String password) {
        return authRepository.login(username, password);
    }
}
