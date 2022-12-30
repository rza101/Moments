package com.pahat.moments.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ChatRoom implements Parcelable {
    private String username;
    private String fullname;
    private String profilePicture;
    private String message;
    private long time;

    public ChatRoom() {
    }

    public ChatRoom(String username, String fullname, String profilePicture, String message, long time) {
        this.username = username;
        this.fullname = fullname;
        this.profilePicture = profilePicture;
        this.message = message;
        this.time = time;
    }

    protected ChatRoom(Parcel in) {
        username = in.readString();
        fullname = in.readString();
        profilePicture = in.readString();
        message = in.readString();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(fullname);
        dest.writeString(profilePicture);
        dest.writeString(message);
        dest.writeLong(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(username, chatRoom.username) && Objects.equals(fullname, chatRoom.fullname) && Objects.equals(profilePicture, chatRoom.profilePicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, fullname, profilePicture);
    }
}
