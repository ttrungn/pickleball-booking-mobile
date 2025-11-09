package com.example.fptstadium.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private DatabaseReference databaseReference;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String currentUserId = "user_1"; // Default user ID
    private String currentUserName = "You";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadMessages();
        setupSendButton();
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void initializeFirebase() {
        databaseReference = FirebaseDatabase.getInstance("https://fptstadium-23ecb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("messages");
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        message.setMessageId(snapshot.getKey());
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                // Fix: Only scroll if there are messages to prevent crash
                if (messageList.size() > 0) {
                    recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
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
        String messageId = databaseReference.push().getKey();
        Message message = new Message(currentUserId, currentUserName, messageText, System.currentTimeMillis());

        if (messageId != null) {
            databaseReference.child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        etMessageInput.setText("");
                        Toast.makeText(ChatActivity.this, "Message sent ✓", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
