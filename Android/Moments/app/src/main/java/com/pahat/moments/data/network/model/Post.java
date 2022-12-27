package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Post implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("username")
    private String username;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("caption")
    private String caption;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public Post() {
    }

    public Post(String username, String imageUrl, String caption) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.caption = caption;
    }

    public Post(long id, String username, String imageUrl, String caption, String createdAt, String updatedAt) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Post(Parcel in) {
        id = in.readLong();
        username = in.readString();
        imageUrl = in.readString();
        caption = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(username);
        parcel.writeString(imageUrl);
        parcel.writeString(caption);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
    }
}
