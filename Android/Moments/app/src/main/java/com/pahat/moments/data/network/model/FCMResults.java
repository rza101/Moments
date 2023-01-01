package com.pahat.moments.data.network.model;

import com.google.gson.annotations.SerializedName;

public class FCMResults {
    @SerializedName("message_id")
    private String messageId;

    @SerializedName("error")
    private String error;

    public FCMResults() {
    }

    public String getMessageId() {
        return messageId;
    }

    public String getError() {
        return error;
    }
}
