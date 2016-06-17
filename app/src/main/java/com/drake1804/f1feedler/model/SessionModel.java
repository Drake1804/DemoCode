package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
@Deprecated
public class SessionModel {

    @PrimaryKey
    @SerializedName("user_id")
    private String userId;

    @SerializedName("access_token")
    private String token;

    @SerializedName("refresh_token")
    private String refreshToken;


    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
