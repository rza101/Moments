package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    private long id;
    private String user_id;
    private String image_url;
    private String caption;
    private String created_at;
    private String updated_at;

    public Post() {
    }

    public Post(String user_id, String image_url, String caption) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.caption = caption;
    }

    protected Post(Parcel in) {
        id = in.readLong();
        user_id = in.readString();
        image_url = in.readString();
        caption = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
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

    public String getUser_id() {
        return user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getCaption() {
        return caption;
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
        parcel.writeString(image_url);
        parcel.writeString(caption);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
