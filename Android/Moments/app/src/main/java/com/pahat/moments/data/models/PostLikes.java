package com.pahat.moments.data.models;

import com.google.gson.annotations.SerializedName;

public class PostLikes {
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("post_id")
    private Integer post_id;

    public PostLikes(Integer user_id, Integer post_id) {
        this.user_id = user_id;
        this.post_id = post_id;
    }

    public PostLikes() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }
}
