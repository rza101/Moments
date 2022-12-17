package com.pahat.moments.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIUtil {
    private static final String API_BASE_URL = "https://rhezarijaya.000webhostapp.com/api";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit != null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(API_BASE_URL)
                    .build();
        }

        return retrofit;
    }
}
