package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserFollow implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_following")
    private String userFollowing;

    public UserFollow() {
    }

    public UserFollow(String userId, String userFollowing) {
        this.userId = userId;
        this.userFollowing = userFollowing;
    }

    protected UserFollow(Parcel in) {
        id = in.readLong();
        userId = in.readString();
        userFollowing = in.readString();
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

    public String getUserId() {
        return userId;
    }

    public String getUserFollowing() {
        return userFollowing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(userId);
        dest.writeString(userFollowing);
    }
}
