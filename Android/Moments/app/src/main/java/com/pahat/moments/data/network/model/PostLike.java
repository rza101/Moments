package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PostLike implements Parcelable {
    private long id;
    private long post_id;
    private String user_id;
    private String created_at;
    private String updated_at;

    public PostLike() {
    }

    public PostLike(long post_id, String user_id) {
        this.post_id = post_id;
        this.user_id = user_id;
    }

    protected PostLike(Parcel in) {
        id = in.readLong();
        post_id = in.readLong();
        user_id = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
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

    public long getPost_id() {
        return post_id;
    }

    public String getUser_id() {
        return user_id;
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
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
