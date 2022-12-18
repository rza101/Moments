package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String user_id;
    private String full_name;
    private String profile_picture;

    public User() {
    }

    public User(String full_name, String profile_picture) {
        this.full_name = full_name;
        this.profile_picture = profile_picture;
    }

    public User(String user_id, String full_name, String profile_picture) {
        this.user_id = user_id;
        this.full_name = full_name;
        this.profile_picture = profile_picture;
    }

    protected User(Parcel in) {
        user_id = in.readString();
        full_name = in.readString();
        profile_picture = in.readString();
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

    public String getUser_id() {
        return user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(full_name);
        dest.writeString(profile_picture);
    }
}
