package com.pahat.moments.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {
    private String chatId;
    private String sender;
    private String senderFullName;
    private String senderProfilePicture;
    private String receiver;
    private String receiverFullName;
    private String receiverProfilePicture;
    private String senderReceiver;
    private String message;
    private String imageUrl;
    private long timestamp;

    public Chat() {
    }

    public Chat(String sender,
                String senderFullName,
                String senderProfilePicture,
                String receiver,
                String receiverFullName,
                String receiverProfilePicture,
                String message,
                String imageUrl,
                long timestamp) {
        this.sender = sender;
        this.senderFullName = senderFullName;
        this.senderProfilePicture = senderProfilePicture;
        this.receiver = receiver;
        this.receiverFullName = receiverFullName;
        this.receiverProfilePicture = receiverProfilePicture;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    protected Chat(Parcel in) {
        chatId = in.readString();
        sender = in.readString();
        senderFullName = in.readString();
        senderProfilePicture = in.readString();
        receiver = in.readString();
        receiverFullName = in.readString();
        receiverProfilePicture = in.readString();
        message = in.readString();
        imageUrl = in.readString();
        timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(sender);
        dest.writeString(senderFullName);
        dest.writeString(senderProfilePicture);
        dest.writeString(receiver);
        dest.writeString(receiverFullName);
        dest.writeString(receiverProfilePicture);
        dest.writeString(message);
        dest.writeString(imageUrl);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public String getSenderProfilePicture() {
        return senderProfilePicture;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public String getReceiverProfilePicture() {
        return receiverProfilePicture;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
