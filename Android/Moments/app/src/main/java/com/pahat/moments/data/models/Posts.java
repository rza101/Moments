package com.pahat.moments.data.models;

import com.google.gson.annotations.SerializedName;

public class Posts {
    @SerializedName("user_id")
    private Integer user_id;
    @SerializedName("image_url")
    private Integer image_url;
    @SerializedName("caption")
    private String caption;

    public Posts(Integer user_id, Integer image_url, String caption) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.caption = caption;
    }

    public Posts() {
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getImage_url() {
        return image_url;
    }

    public void setImage_url(Integer image_url) {
        this.image_url = image_url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
