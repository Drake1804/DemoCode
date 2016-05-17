package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class CommentModel extends RealmObject {

    @SerializedName("newsId")
    private String newsId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("text")
    private String text;

}
