package com.pahat.moments.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {
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
    private String chatId;
    private String roomId;
    private String sender;
    private String message;
    private String imageUrl;
    private long timestamp;

    public Chat() {
    }

    public Chat(String sender,
                String message,
                String imageUrl,
                long timestamp) {
        this.sender = sender;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    protected Chat(Parcel in) {
        chatId = in.readString();
        sender = in.readString();
        message = in.readString();
        imageUrl = in.readString();
        timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(sender);
        dest.writeString(message);
        dest.writeString(imageUrl);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
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
}
