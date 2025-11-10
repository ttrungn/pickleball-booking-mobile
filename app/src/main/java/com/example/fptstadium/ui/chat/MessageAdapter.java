package com.example.fptstadium.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvSenderName;
        private TextView tvTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Message message) {
            // Use new getMessage() method instead of deprecated getMessageText()
            String messageText = message.getMessage();
            if (messageText != null) {
                tvMessageText.setText(messageText);
            } else {
                // Fallback for backward compatibility
                tvMessageText.setText(message.getMessageText());
            }
            
            // Set sender name, handle null
            if (message.getSenderName() != null && !message.getSenderName().isEmpty()) {
                String displayName = message.getSenderName();
                // Add role indicator if available
                if (message.getSenderRole() != null) {
                    if (message.getSenderRole().equals("admin")) {
                        displayName += " (Admin)";
                    }
                }
                tvSenderName.setText(displayName);
            } else {
                tvSenderName.setText("Unknown");
            }
            
            // Set timestamp with proper validation
            tvTimestamp.setText(formatTimestamp(message.getTimestamp()));
        }

        private String formatTimestamp(long timestamp) {
            // Check if timestamp is valid (not 0 or negative)
            if (timestamp <= 0) {
                return "Just now";
            }
            
            try {
                // Check if timestamp is in seconds (older data) vs milliseconds
                // Timestamps before year 2001 in milliseconds are likely in seconds
                long actualTimestamp = timestamp;
                if (timestamp < 1000000000000L) {
                    // This is likely in seconds, convert to milliseconds
                    actualTimestamp = timestamp * 1000;
                }
                
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return sdf.format(new Date(actualTimestamp));
            } catch (Exception e) {
                return "Just now";
            }
        }
    }
}
