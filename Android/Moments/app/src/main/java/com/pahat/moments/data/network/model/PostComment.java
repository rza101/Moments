package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PostComment implements Parcelable {
    @SerializedName("id")
    private long id;

    @SerializedName("post_id")
    private long postId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public PostComment() {
    }

    public PostComment(long postId, String userId, String comment) {
        this.postId = postId;
        this.userId = userId;
        this.comment = comment;
    }

    protected PostComment(Parcel in) {
        id = in.readLong();
        postId = in.readLong();
        userId = in.readString();
        comment = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

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

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(postId);
        parcel.writeString(userId);
        parcel.writeString(comment);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
    }
}
