package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserFollow implements Parcelable {
    private long id;
    private String user_id;
    private String user_following;

    public UserFollow() {
    }

    public UserFollow(String user_id, String user_following) {
        this.user_id = user_id;
        this.user_following = user_following;
    }

    protected UserFollow(Parcel in) {
        id = in.readLong();
        user_id = in.readString();
        user_following = in.readString();
    }

    public static final Creator<UserFollow> CREATOR = new Creator<UserFollow>() {
        @Override
        public UserFollow createFromParcel(Parcel in) {
            return new UserFollow(in);
        }

        @Override
        public UserFollow[] newArray(int size) {
            return new UserFollow[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_following() {
        return user_following;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(user_id);
        dest.writeString(user_following);
    }
}
