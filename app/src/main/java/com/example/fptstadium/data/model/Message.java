package com.example.fptstadium.data.model;

public class Message {
    private String id;
    private String senderId;
    private String senderName;
    private String senderRole; // "admin" or "customer"
    private String message;
    private long timestamp;
    private boolean read;

    // Default constructor for Firebase
    public Message() {
    }

    public Message(String senderId, String senderName, String senderRole, String message, long timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.timestamp = timestamp;
        this.read = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    // Backward compatibility methods
    @Deprecated
    public String getMessageId() {
        return id;
    }

    @Deprecated
    public void setMessageId(String messageId) {
        this.id = messageId;
    }

    @Deprecated
    public String getMessageText() {
        return message;
    }

    @Deprecated
    public void setMessageText(String messageText) {
        this.message = messageText;
    }
}
