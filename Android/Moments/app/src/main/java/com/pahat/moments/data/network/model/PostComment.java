package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PostComment implements Parcelable {
    public static final Creator<PostComment> CREATOR = new Creator<PostComment>() {
        @Override
        public PostComment createFromParcel(Parcel in) {
            return new PostComment(in);
        }

        @Override
        public PostComment[] newArray(int size) {
            return new PostComment[size];
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
    @SerializedName("comment")
    private String comment;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public PostComment() {
    }

    public PostComment(long postId, String username, String comment) {
        this.postId = postId;
        this.username = username;
        this.comment = comment;
    }

    protected PostComment(Parcel in) {
        id = in.readLong();
        postId = in.readLong();
        username = in.readString();
        fullName = in.readString();
        imageUrl = in.readString();
        comment = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
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

    public String getComment() {
        return comment;
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
        dest.writeLong(postId);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(imageUrl);
        dest.writeString(comment);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}
