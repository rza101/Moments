package com.pahat.moments.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostComposite {
    @SerializedName("post")
    private List<Post> post;

    @SerializedName("post_like")
    private List<PostLike> postLike;

    @SerializedName("post_comment")
    private List<PostLike> postComment;

    public PostComposite() {
    }

    public List<Post> getPost() {
        return post;
    }

    public List<PostLike> getPostLike() {
        return postLike;
    }

    public List<PostLike> getPostComment() {
        return postComment;
    }
}
