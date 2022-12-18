package com.pahat.moments.data.network.model;

import java.util.List;

public class UserFollowComposite {
    private List<UserFollow> follower;
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
