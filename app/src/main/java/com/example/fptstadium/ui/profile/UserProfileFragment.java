package com.example.fptstadium.ui.profile;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fptstadium.R;
import com.example.fptstadium.databinding.FragmentUserProfileBinding;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        UserProfileViewModel userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}