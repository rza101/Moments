package com.pahat.moments.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class ChatRoom implements Parcelable {
    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };
    private String chatRoomId;
    private String lastMessage;
    private long lastMessageTimestamp;
    private Map<String, User> participants;

    public ChatRoom() {
    }

    public ChatRoom(Map<String, User> participants) {
        this.participants = participants;
    }

    public ChatRoom(String lastMessage, long lastMessageTimestamp) {
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public ChatRoom(String lastMessage, long lastMessageTimestamp, Map<String, User> participants) {
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.participants = participants;
    }

    protected ChatRoom(Parcel in) {
        chatRoomId = in.readString();
        lastMessage = in.readString();
        lastMessageTimestamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatRoomId);
        dest.writeString(lastMessage);
        dest.writeLong(lastMessageTimestamp);
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public Map<String, User> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomId='" + chatRoomId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageTimestamp=" + lastMessageTimestamp +
                ", participants=" + participants +
                '}';
    }
}
