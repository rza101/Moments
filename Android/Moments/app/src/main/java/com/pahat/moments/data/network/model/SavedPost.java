package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SavedPost implements Parcelable {
    private long id;
    private String user_id;
    private long post_id;
    private String created_at;
    private String updated_at;

    public SavedPost() {
    }

    public SavedPost(String user_id, long post_id) {
        this.user_id = user_id;
        this.post_id = post_id;
    }

    protected SavedPost(Parcel in) {
        id = in.readLong();
        user_id = in.readString();
        post_id = in.readLong();
        created_at = in.readString();
        updated_at = in.readString();
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

    public String getUser_id() {
        return user_id;
    }

    public long getPost_id() {
        return post_id;
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
        parcel.writeString(user_id);
        parcel.writeLong(post_id);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
