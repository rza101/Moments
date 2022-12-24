package com.pahat.moments.data.models;

import com.google.gson.annotations.SerializedName;

public class UserFollow {
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("user_following")
    private Integer user_following;

    public UserFollow(Integer user_id, Integer user_following) {
        this.user_id = user_id;
        this.user_following = user_following;
    }

    public UserFollow() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_following() {
        return user_following;
    }

    public void setUser_following(Integer user_following) {
        this.user_following = user_following;
    }
}
