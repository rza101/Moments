package com.pahat.moments.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserFollowComposite {
    @SerializedName("follower")
    private List<UserFollow> follower;

    @SerializedName("following")
    private List<UserFollow> following;

    public UserFollowComposite() {
    }

    public List<UserFollow> getFollower() {
        return follower;
    }

    public List<UserFollow> getFollowing() {
        return following;
    }
}
