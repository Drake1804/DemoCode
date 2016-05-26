package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Pavel.Shkaran on 5/26/2016.
 */
public class ResourceModel extends RealmObject {

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("image_url")
    private String imageUrl;



    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
