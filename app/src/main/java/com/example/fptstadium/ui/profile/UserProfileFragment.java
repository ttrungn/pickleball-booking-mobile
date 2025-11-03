package com.example.fptstadium.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.response.UserProfileData;
import com.example.fptstadium.ui.auth.AuthViewModel;
import com.example.fptstadium.ui.auth.LoginActivity;
import com.example.fptstadium.utils.PrefsHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserProfileFragment extends Fragment {

    private static final int UPDATE_PROFILE_REQUEST_CODE = 1001;

    @Inject
    PrefsHelper prefsHelper;

    private TextView tvUserName, tvUserEmail;
    private LinearLayout llChangeProfile, llChangePassword, llAboutUs, llLogout, llTestPayment;
    private AuthViewModel authViewModel;
    private PaymentViewModel paymentViewModel;
    private UserProfileData currentUserData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initViewModel();
        setupListeners();
        loadUserInfo();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        llChangeProfile = view.findViewById(R.id.llChangeProfile);
        llChangePassword = view.findViewById(R.id.llChangePassword);
        llAboutUs = view.findViewById(R.id.llAboutUs);
        llLogout = view.findViewById(R.id.llLogout);
        llTestPayment = view.findViewById(R.id.llTestPayment);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
    }

    private void setupListeners() {
        llChangeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
            if (currentUserData != null) {
                intent.putExtra("firstName", currentUserData.getFirstName());
                intent.putExtra("lastName", currentUserData.getLastName());
                intent.putExtra("phoneNumber", currentUserData.getPhoneNumber());
            }
            startActivityForResult(intent, UPDATE_PROFILE_REQUEST_CODE);
        });

        llChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Change Password clicked", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to change password activity
        });

        llAboutUs.setOnClickListener(v -> {
            Toast.makeText(getContext(), "About Us clicked", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to about us activity
        });

        llLogout.setOnClickListener(v -> {
            logout();
        });

        if (llTestPayment != null) {
            llTestPayment.setOnClickListener(v -> {
                String bookingId = java.util.UUID.randomUUID().toString();
                paymentViewModel.getMomoPaymentUrl(bookingId).observe(getViewLifecycleOwner(), response -> {
                    if (response != null && response.isSuccess() && response.getData() != null) {
                        String deeplink = response.getData().getDeeplink();
                        String payUrl = response.getData().getPayUrl();

                        // Try opening MoMo app via deeplink first
                        if (deeplink != null && !deeplink.isEmpty()) {
                            try {
                                Intent momoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
                                startActivity(momoIntent);
                            } catch (Exception ignored) {
                                // Fall through to payUrl/web fallback
                            }
                        }

                        // Fallback to opening payUrl in browser if deeplink/app not available
//                        if (payUrl != null && !payUrl.isEmpty()) {
//                            try {
//                                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
//                                startActivity(webIntent);
//                            } catch (Exception e) {
//                                Toast.makeText(getContext(), "Unable to open payment link", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "No payment link available", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to create Momo payment", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void loadUserInfo() {
        authViewModel.getUserProfile().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                currentUserData = response.getData();
                tvUserName.setText(currentUserData.getFirstName() + " " + currentUserData.getLastName());
                tvUserEmail.setText(currentUserData.getEmail());
            } else {
                tvUserName.setText("User Name");
                tvUserEmail.setText("user@email.com");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // If update was successful, reload user profile data
        if (requestCode == UPDATE_PROFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Reload user info from server
            loadUserInfo();
        }
    }

    private void logout() {
        // Clear all saved tokens
        prefsHelper.clearAllTokens();
        
        // Navigate back to login
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}