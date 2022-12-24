package com.pahat.moments.data.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("username")
    private Integer username;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("profile_picture")
    private String profile_picture;

    public User(Integer user_id, Integer username, String full_name, String profile_picture) {
        this.user_id = user_id;
        this.username = username;
        this.full_name = full_name;
        this.profile_picture = profile_picture;
    }

    public User() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUsername() {
        return username;
    }

    public void setUsername(Integer username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
