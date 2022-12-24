package com.pahat.moments.data.models;

import com.google.gson.annotations.SerializedName;

public class PostComments {
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("post_id")
    private Integer post_id;
    @SerializedName("comment")
    private String comment;

    public PostComments(Integer user_id, Integer post_id, String comment) {
        this.user_id = user_id;
        this.post_id = post_id;
        this.comment = comment;
    }

    public PostComments() {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
