package com.example.fptstadium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.fptstadium.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the action bar completely
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Xử lý khi mở từ notification
        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    /**
     * Xử lý intent từ notification
     * Nếu có flag open_my_bookings = true, mở tab My Bookings
     */
    private void handleNotificationIntent(Intent intent) {
        if (intent != null) {
            boolean openMyBookings = intent.getBooleanExtra("open_my_bookings", false);
            int bookingId = intent.getIntExtra("booking_id", 0);

            if (openMyBookings) {
                Log.d(TAG, "Opening from notification, bookingId: " + bookingId);

                // Navigate to dashboard/notifications tab
                // Thay đổi navigation_notifications thành ID phù hợp với tab My Bookings của bạn
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_notifications);

                // Clear intent extras để tránh xử lý lại khi rotate screen
                intent.removeExtra("open_my_bookings");
                intent.removeExtra("booking_id");
            }
        }
    }
}