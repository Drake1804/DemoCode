package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ItemSessionResponseModel {

    @SerializedName("token")
    private String mToken;

    @SerializedName("refresh_token")
    private String mRefreshToken;

    @SerializedName("token_expires_at")
    private Date mTokenExpiresAt;

    public String getToken() {
        return mToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public Date getTokenExpiresAt() {
        return mTokenExpiresAt;
    }

}
