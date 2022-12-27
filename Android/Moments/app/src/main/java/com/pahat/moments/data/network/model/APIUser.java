package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class APIUser implements Parcelable {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("fcm_token")
    private String fcmToken;

    public APIUser() {
    }

    public APIUser(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public APIUser(String username, String fullName, String imageUrl) {
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    public APIUser(String userId, String username, String fullName, String imageUrl, String fcmToken) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.fcmToken = fcmToken;
    }

    protected APIUser(Parcel in) {
        userId = in.readString();
        username = in.readString();
        fullName = in.readString();
        imageUrl = in.readString();
        fcmToken = in.readString();
    }

    public static final Creator<APIUser> CREATOR = new Creator<APIUser>() {
        @Override
        public APIUser createFromParcel(Parcel in) {
            return new APIUser(in);
        }

        @Override
        public APIUser[] newArray(int size) {
            return new APIUser[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getFullName() {
        return fullName;
    }

    public String getImageUrl() {
        return imageUrl;
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
        dest.writeString(imageUrl);
        dest.writeString(fcmToken);
    }
}
