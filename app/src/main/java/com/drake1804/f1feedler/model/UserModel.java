package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class UserModel extends RealmObject {

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String username;


    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
