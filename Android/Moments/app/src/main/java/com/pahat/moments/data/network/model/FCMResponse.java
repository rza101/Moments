package com.pahat.moments.data.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FCMResponse {
    @SerializedName("multicast_id")
    private long multicastId;

    @SerializedName("success")
    private int success;

    @SerializedName("failure")
    private int failure;

    @SerializedName("canonical_ids")
    private int canonicalIds;

    @SerializedName("results")
    private List<FCMResults> results;

    public FCMResponse() {
    }

    public long getMulticastId() {
        return multicastId;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public int getCanonicalIds() {
        return canonicalIds;
    }

    public List<FCMResults> getResults() {
        return results;
    }
}
