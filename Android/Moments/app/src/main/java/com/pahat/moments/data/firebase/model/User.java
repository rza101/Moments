package com.pahat.moments.data.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId;
    private String username;
    private String fullName;
    private String profilePicture;

    public User() {
    }

    public User(String fullName, String profilePicture) {
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    public User(String userId, String username, String fullName, String profilePicture) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    protected User(Parcel in) {
        userId = in.readString();
        username = in.readString();
        fullName = in.readString();
        profilePicture = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(profilePicture);
    }
}