package com.example.fptstadium.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fptstadium.MainActivity;
import com.example.fptstadium.R;
import com.example.fptstadium.utils.PrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.HashMap;
import java.util.Map;


@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private AuthViewModel viewModel;

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn;
    private TextView tvSignUp, forgotPassword;
    private DatabaseReference databaseReference;

    @Inject
    PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        if (prefsHelper.getToken() != null) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//            return;
//        }

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://fptstadium-23ecb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        forgotPassword = findViewById(R.id.forgotPassword);

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }
            btnSignIn.setEnabled(false);
            viewModel.login(email, password).observe(this, loginResponse -> {
                btnSignIn.setEnabled(true);

                if (loginResponse != null && loginResponse.isSuccess()) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Test Firebase Realtime Database - Send simple data
                    sendTestDataToFirebase(email);

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignUp.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show());
    }

    /**
     * Send test data to Firebase Realtime Database
     */
    private void sendTestDataToFirebase(String email) {
        try {
            // Create a simple test data object
            Map<String, Object> testData = new HashMap<>();
            testData.put("email", email);
            testData.put("loginTime", System.currentTimeMillis());
            testData.put("testMessage", "Login test data from " + email);
            testData.put("status", "logged_in");

            // Send data to Firebase under /test_logins/{timestamp}
            String timestamp = String.valueOf(System.currentTimeMillis());
            databaseReference.child("test_logins").child(timestamp).setValue(testData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(LoginActivity.this, "Test data sent to Firebase ✓", Toast.LENGTH_SHORT).show();
                        System.out.println("✓ Firebase Test: Data sent successfully at path: /test_logins/" + timestamp);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Failed to send test data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("✗ Firebase Test Error: " + e.getMessage());
                    });

        } catch (Exception e) {
            System.out.println("✗ Exception in sendTestDataToFirebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}