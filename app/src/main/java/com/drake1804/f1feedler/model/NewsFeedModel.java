package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class NewsFeedModel extends RealmObject {

    @PrimaryKey
    @SerializedName("uuid")
    private String uuid;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("link")
    private String link;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("pub_date")
    private Date creatingDate;

    @SerializedName("resource")
    private ResourceModel resource;

    @SerializedName("social")
    private SocialModel social;

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getCreatingDate() {
        return creatingDate;
    }

    public ResourceModel getResource() {
        return resource;
    }

    public SocialModel getSocial() {
        return social;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreatingDate(Date creatingDate) {
        this.creatingDate = creatingDate;
    }

    public void setResource(ResourceModel resource) {
        this.resource = resource;
    }

    public void setSocial(SocialModel social) {
        this.social = social;
    }
}
