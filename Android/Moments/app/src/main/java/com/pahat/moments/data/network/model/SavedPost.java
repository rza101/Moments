package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SavedPost implements Parcelable {
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
    @SerializedName("id")
    private long id;
    @SerializedName("username")
    private String username;
    @SerializedName("post_id")
    private long postId;
    @SerializedName("post_username")
    private String postUsername;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("caption")
    private String caption;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public SavedPost() {
    }

    public SavedPost(String username, long postId) {
        this.username = username;
        this.postId = postId;
    }

    protected SavedPost(Parcel in) {
        id = in.readLong();
        username = in.readString();
        postId = in.readLong();
        postUsername = in.readString();
        imageUrl = in.readString();
        caption = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public long getPostId() {
        return postId;
    }

    public String getPostUsername() {
        return postUsername;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCaption() {
        return caption;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(username);
        dest.writeLong(postId);
        dest.writeString(postUsername);
        dest.writeString(imageUrl);
        dest.writeString(caption);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}
