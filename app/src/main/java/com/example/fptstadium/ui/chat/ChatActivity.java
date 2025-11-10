package com.example.fptstadium.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.Message;
import com.example.fptstadium.utils.PrefsHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private DatabaseReference messagesRef;
    private DatabaseReference chatRoomRef;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentUserId;
    private String currentUserName;
    private String currentUserEmail;

    @Inject
    PrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeUserInfo();
        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadMessages();
        setupSendButton();
    }

    private void initializeUserInfo() {
        // Get user info from PrefsHelper (populated from UserProfileFragment)
        currentUserId = prefsHelper.getUserId();
        currentUserName = prefsHelper.getUserName();
        currentUserEmail = prefsHelper.getUserEmail();

        Log.d("ChatActivity", "Loaded from Prefs - ID: " + currentUserId + 
              ", Name: " + currentUserName + ", Email: " + currentUserEmail);

        // Check if user info is available from backend profile
        if (currentUserId == null || currentUserId.isEmpty()) {
            // User hasn't loaded profile yet - show warning
            Toast.makeText(this, "Loading user profile...", Toast.LENGTH_SHORT).show();
            
            // Create temporary ID (will be replaced when profile loads)
            currentUserId = "temp_" + System.currentTimeMillis();
            Log.w("ChatActivity", "Using temporary user ID. Please ensure user profile is loaded first.");
        }
        
        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = "Guest User";
        }
        
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            currentUserEmail = "guest@example.com";
        }

        Log.d("ChatActivity", "Final User Info - ID: " + currentUserId + 
              ", Name: " + currentUserName + ", Email: " + currentUserEmail);
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        
        // Setup back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void initializeFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fptstadium-23ecb-default-rtdb.asia-southeast1.firebasedatabase.app/");
        
        // Reference to chat room messages: chatRooms/{userId}/messages
        chatRoomRef = database.getReference("chatRooms").child(currentUserId);
        messagesRef = chatRoomRef.child("messages");
        
        // Initialize or update chat room info
        updateChatRoomInfo();
    }

    private void updateChatRoomInfo() {
        Map<String, Object> chatRoomData = new HashMap<>();
        chatRoomData.put("id", currentUserId);
        chatRoomData.put("customerId", currentUserId);
        chatRoomData.put("customerName", currentUserName);
        chatRoomData.put("customerEmail", currentUserEmail);
        chatRoomData.put("isOnline", true);
        
        chatRoomRef.updateChildren(chatRoomData);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                List<String> unreadAdminMessageIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        message.setId(snapshot.getKey());
                        // Log timestamp for debugging
                        Log.d("ChatActivity", "Message timestamp: " + message.getTimestamp() + 
                              ", Sender: " + message.getSenderName());
                        // Collect unread admin messages to mark as read
                        Object roleObj = snapshot.child("senderRole").getValue();
                        Object readObj = snapshot.child("read").getValue();
                        boolean isAdmin = roleObj != null && "admin".equals(roleObj.toString());
                        boolean isRead = readObj != null && Boolean.TRUE.equals(readObj);
                        if (isAdmin && !isRead) {
                            unreadAdminMessageIds.add(snapshot.getKey());
                        }
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                // Fix: Only scroll if there are messages to prevent crash
                if (messageList.size() > 0) {
                    recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
                }

                // Mark admin messages as read once viewed
                if (!unreadAdminMessageIds.isEmpty()) {
                    for (String id : unreadAdminMessageIds) {
                        messagesRef.child(id).child("read").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSendButton() {
        btnSendMessage.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String messageText) {
        String messageId = messagesRef.push().getKey();
        long timestamp = System.currentTimeMillis();
        
        // Create message with correct structure matching ChatMessage interface
        Message message = new Message(
            currentUserId, 
            currentUserName, 
            "customer",  // senderRole
            messageText, 
            timestamp
        );

        if (messageId != null) {
            messagesRef.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        etMessageInput.setText("");
                        // Update chat room's last message info
                        updateLastMessage(messageText, timestamp);
                        Toast.makeText(ChatActivity.this, "Message sent success", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateLastMessage(String messageText, long timestamp) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", messageText);
        updates.put("lastMessageTimestamp", timestamp);
        updates.put("unreadCount", 0); // Reset for customer, admin will increment
        
        chatRoomRef.updateChildren(updates);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update online status
        if (chatRoomRef != null) {
            chatRoomRef.child("isOnline").setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update online status and last seen
        if (chatRoomRef != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("isOnline", false);
            updates.put("lastSeen", System.currentTimeMillis());
            chatRoomRef.updateChildren(updates);
        }
    }
}
