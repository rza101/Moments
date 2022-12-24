package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PostLike implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("post_id")
    private long postId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public PostLike() {
    }

    public PostLike(long postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    protected PostLike(Parcel in) {
        id = in.readLong();
        postId = in.readLong();
        userId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<PostLike> CREATOR = new Creator<PostLike>() {
        @Override
        public PostLike createFromParcel(Parcel in) {
            return new PostLike(in);
        }

        @Override
        public PostLike[] newArray(int size) {
            return new PostLike[size];
        }
    };

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
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
        parcel.writeLong(postId);
        parcel.writeString(userId);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
    }
}
