package com.example.fptstadium.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.response.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etFirstName, etLastName, etEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private TextView tvSignIn;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupListeners() {
        btnSignUp.setOnClickListener(v -> performRegistration());
        tvSignIn.setOnClickListener(v -> navigateToLogin());
    }

    private void performRegistration() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(firstName, lastName, email, phoneNumber, password, confirmPassword)) {
            return;
        }

        showProgressBar(true);
        authViewModel.register(email, password, confirmPassword, firstName, lastName, phoneNumber)
                .observe(this, response -> {
                    showProgressBar(false);
                    if (response != null) {
                        if (response.isSuccess()) {
                            Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        } else {
                            Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String firstName, String lastName, String email, 
                                  String phoneNumber, String password, String confirmPassword) {
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        }
        if (email.isEmpty() || !isValidEmail(email)) {
            etEmail.setError("Valid email is required");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone number is required");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showProgressBar(boolean show) {
        btnSignUp.setEnabled(!show);
        if (show) {
            btnSignUp.setText("Registering...");
        } else {
            btnSignUp.setText("SIGN UP");
        }
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}