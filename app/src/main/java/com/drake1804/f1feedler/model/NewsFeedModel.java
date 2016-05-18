package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class NewsFeedModel extends RealmObject {

    @PrimaryKey
    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("creatingDate")
    private String creatingDate;

    @SerializedName("imageUrl")
    private String imageUrl;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getCreatingDate() {
        return creatingDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCreatingDate(String creatingDate) {
        this.creatingDate = creatingDate;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
