package com.pahat.moments.data.network.model;

public class ChatMessage {
    private String id;
    private String text;
    private String sender;
    private String imageUrl;

    public ChatMessage() {
    }

    public ChatMessage(String text, String sender, String imageUrl) {
        this.text = text;
        this.sender = sender;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
