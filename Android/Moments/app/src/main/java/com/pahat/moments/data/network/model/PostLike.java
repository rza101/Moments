package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PostLike implements Parcelable {
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
    @SerializedName("id")
    private long id;
    @SerializedName("post_id")
    private long postId;
    @SerializedName("username")
    private String username;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public PostLike() {
    }

    public PostLike(long postId, String username) {
        this.postId = postId;
        this.username = username;
    }

    protected PostLike(Parcel in) {
        id = in.readLong();
        postId = in.readLong();
        username = in.readString();
        fullName = in.readString();
        imageUrl = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(postId);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(imageUrl);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
