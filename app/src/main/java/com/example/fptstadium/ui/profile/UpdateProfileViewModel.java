package com.example.fptstadium.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.fptstadium.data.model.response.UpdateProfileResponse;
import com.example.fptstadium.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UpdateProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public UpdateProfileViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<UpdateProfileResponse> updateProfile(String firstName, String lastName, String phoneNumber) {
        return authRepository.updateProfile(firstName, lastName, phoneNumber);
    }
}
