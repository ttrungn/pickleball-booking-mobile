package com.example.fptstadium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fptstadium.databinding.ActivityMainBinding;
import com.example.fptstadium.ui.auth.AuthViewModel;
import com.example.fptstadium.ui.chat.ChatActivity;
import com.example.fptstadium.utils.PrefsHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    @Inject
    PrefsHelper prefsHelper;
    
    private AuthViewModel authViewModel;
    private com.google.firebase.database.DatabaseReference messagesRef;
    private com.google.firebase.database.ValueEventListener unreadListener;
    private TextView badgeChatCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the action bar completely
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Preload user profile for chat functionality
        preloadUserProfile();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_user_profile)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Setup Chat Button and Floating Action Button
        setupChatButtons();
        setupUnreadBadge();
    }

    private void preloadUserProfile() {
        // Fetch user profile on app start to ensure chat has user info
        authViewModel.getUserProfile().observe(this, response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                var userData = response.getData();
                
                // Save to PrefsHelper for chat
                if (userData.getId() != null) {
                    prefsHelper.saveUserId(userData.getId());
                }
                
                String fullName = (userData.getFirstName() != null ? userData.getFirstName() : "") + 
                                 " " + 
                                 (userData.getLastName() != null ? userData.getLastName() : "");
                prefsHelper.saveUserName(fullName.trim());
                
                if (userData.getEmail() != null) {
                    prefsHelper.saveUserEmail(userData.getEmail());
                }
            }
        });
    }

    private void setupChatButtons() {
        // Setup Floating Action Button only
        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        if (fabChat != null) {
            fabChat.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupUnreadBadge() {
        badgeChatCount = findViewById(R.id.badgeChatCount);
        String userId = prefsHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            // Will try again after profile loads
            return;
        }
        com.google.firebase.database.FirebaseDatabase db = com.google.firebase.database.FirebaseDatabase.getInstance("https://fptstadium-23ecb-default-rtdb.asia-southeast1.firebasedatabase.app/");
        messagesRef = db.getReference("chatRooms").child(userId).child("messages");

        unreadListener = new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                int unread = 0;
                for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                    Object senderRoleObj = child.child("senderRole").getValue();
                    Object readObj = child.child("read").getValue();
                    boolean isAdmin = senderRoleObj != null && "admin".equals(senderRoleObj.toString());
                    boolean isRead = readObj != null && Boolean.TRUE.equals(readObj);
                    if (isAdmin && !isRead) {
                        unread++;
                    }
                }
                if (badgeChatCount != null) {
                    if (unread > 0) {
                        badgeChatCount.setVisibility(View.VISIBLE);
                        badgeChatCount.setText(unread > 99 ? "99+" : String.valueOf(unread));
                    } else {
                        badgeChatCount.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                // Silent fail
            }
        };
        messagesRef.addValueEventListener(unreadListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reattach if needed (e.g. profile loaded after initial attempt)
        if (badgeChatCount == null) {
            setupUnreadBadge();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesRef != null && unreadListener != null) {
            messagesRef.removeEventListener(unreadListener);
        }
    }

}