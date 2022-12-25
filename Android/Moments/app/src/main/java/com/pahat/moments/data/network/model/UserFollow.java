package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserFollow implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("username")
    private String username;

    @SerializedName("username_following")
    private String usernameFollowing;

    public UserFollow() {
    }

    public UserFollow(String username, String usernameFollowing) {
        this.username = username;
        this.usernameFollowing = usernameFollowing;
    }

    protected UserFollow(Parcel in) {
        id = in.readLong();
        username = in.readString();
        usernameFollowing = in.readString();
    }

    public static final Creator<UserFollow> CREATOR = new Creator<UserFollow>() {
        @Override
        public UserFollow createFromParcel(Parcel in) {
            return new UserFollow(in);
        }

        @Override
        public UserFollow[] newArray(int size) {
            return new UserFollow[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameFollowing() {
        return usernameFollowing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(usernameFollowing);
    }
}
