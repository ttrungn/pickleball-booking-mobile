package com.example.fptstadium.data.model;

public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String messageText;
    private long timestamp;
    private String type; // "text", "image", etc.

    // Default constructor for Firebase
    public Message() {
    }

    public Message(String senderId, String senderName, String messageText, long timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.type = "text";
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
