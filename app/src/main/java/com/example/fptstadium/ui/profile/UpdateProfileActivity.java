package com.example.fptstadium.ui.profile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fptstadium.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdateProfileActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhoneNumber;
    private MaterialButton btnUpdate;
    private ImageView ivBack;
    private UpdateProfileViewModel updateProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
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
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnUpdate = findViewById(R.id.btnUpdate);
        ivBack = findViewById(R.id.ivBack);
        
        // Load data from intent if provided
        loadUserDataFromIntent();
    }

    private void loadUserDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String firstName = extras.getString("firstName", "");
            String lastName = extras.getString("lastName", "");
            String phoneNumber = extras.getString("phoneNumber", "");
            
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
            etPhoneNumber.setText(phoneNumber);
        }
    }

    private void initViewModel() {
        updateProfileViewModel = new ViewModelProvider(this).get(UpdateProfileViewModel.class);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        btnUpdate.setOnClickListener(v -> performUpdate());
    }

    private void performUpdate() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (!validateInput(firstName, lastName, phoneNumber)) {
            return;
        }

        showProgressBar(true);
        updateProfileViewModel.updateProfile(firstName, lastName, phoneNumber)
                .observe(this, response -> {
                    showProgressBar(false);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(UpdateProfileActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        // Set result to indicate successful update
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorMessage = response != null ? response.getMessage() : "Failed to update profile";
                        Toast.makeText(UpdateProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String firstName, String lastName, String phoneNumber) {
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone number is required");
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            etPhoneNumber.setError("Please enter a valid phone number");
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Simple phone number validation (at least 9 digits)
        return phoneNumber.replaceAll("[^0-9]", "").length() >= 9;
    }

    private void showProgressBar(boolean show) {
        btnUpdate.setEnabled(!show);
        if (show) {
            btnUpdate.setText("Updating...");
        } else {
            btnUpdate.setText("UPDATE");
        }
    }
}
