package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public class SessionModel {

    @SerializedName("access_token")
    private String token;

    @SerializedName("refresh_token")
    private String refreshToken;

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
