package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserFollow implements Parcelable {
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
    @SerializedName("id")
    private long id;
    @SerializedName("username")
    private String username;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("username_following")
    private String usernameFollowing;
    @SerializedName("full_name_following")
    private String fullNameFollowing;
    @SerializedName("image_url_following")
    private String imageUrlFollowing;

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

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsernameFollowing() {
        return usernameFollowing;
    }

    public String getFullNameFollowing() {
        return fullNameFollowing;
    }

    public String getImageUrlFollowing() {
        return imageUrlFollowing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(imageUrl);
        dest.writeString(usernameFollowing);
        dest.writeString(fullNameFollowing);
        dest.writeString(imageUrlFollowing);
    }
}
