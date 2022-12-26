package com.pahat.moments.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostComposite implements Parcelable {
    @SerializedName("post")
    private Post post;

    @SerializedName("post_like")
    private List<PostLike> postLike;

    @SerializedName("post_comment")
    private List<PostComment> postComment;

    public PostComposite() {
    }

    protected PostComposite(Parcel in) {
        post = in.readParcelable(Post.class.getClassLoader());
        postLike = in.createTypedArrayList(PostLike.CREATOR);
        postComment = in.createTypedArrayList(PostComment.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(post, flags);
        dest.writeTypedList(postLike);
        dest.writeTypedList(postComment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostComposite> CREATOR = new Creator<PostComposite>() {
        @Override
        public PostComposite createFromParcel(Parcel in) {
            return new PostComposite(in);
        }

        @Override
        public PostComposite[] newArray(int size) {
            return new PostComposite[size];
        }
    };

    public Post getPost() {
        return post;
    }

    public List<PostLike> getPostLike() {
        return postLike;
    }

    public List<PostComment> getPostComment() {
        return postComment;
    }
}
