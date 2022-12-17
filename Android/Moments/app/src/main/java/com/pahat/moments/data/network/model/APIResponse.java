package com.pahat.moments.data.network.model;

public class APIResponse<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
