package com.example.gympip;

public class Message {
    private String text;
    private String senderId;
    private String timestamp;

    // Empty constructor required for Firebase
    public Message() {}

    public Message(String text, String senderId, String timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getText() { return text; }
    public String getSenderId() { return senderId; }
    public String getTimestamp() { return timestamp; }

    public void setText(String text) { this.text = text; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

