package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SavedPost implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("post_id")
    private long postId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public SavedPost() {
    }

    public SavedPost(String userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    protected SavedPost(Parcel in) {
        id = in.readLong();
        userId = in.readString();
        postId = in.readLong();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<SavedPost> CREATOR = new Creator<SavedPost>() {
        @Override
        public SavedPost createFromParcel(Parcel in) {
            return new SavedPost(in);
        }

        @Override
        public SavedPost[] newArray(int size) {
            return new SavedPost[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getPostId() {
        return postId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(userId);
        parcel.writeLong(postId);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
    }
}
