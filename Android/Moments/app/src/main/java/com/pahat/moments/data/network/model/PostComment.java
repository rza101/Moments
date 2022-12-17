package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PostComment implements Parcelable {
    private long id;
    private long post_id;
    private String user_id;
    private String comment;
    private String created_at;
    private String updated_at;

    public PostComment() {
    }

    public PostComment(long post_id, String user_id, String comment) {
        this.post_id = post_id;
        this.user_id = user_id;
        this.comment = comment;
    }

    protected PostComment(Parcel in) {
        id = in.readLong();
        post_id = in.readLong();
        user_id = in.readString();
        comment = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
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

    public long getPost_id() {
        return post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(post_id);
        parcel.writeString(user_id);
        parcel.writeString(comment);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
