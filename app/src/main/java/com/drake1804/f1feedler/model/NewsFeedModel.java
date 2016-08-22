package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class NewsFeedModel extends RealmObject {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @PrimaryKey
    @SerializedName("link")
    private String link;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("createdAt")
    private Date creatingDate;

    /*@SerializedName("resource")
    private ResourceModel resource;

    @SerializedName("social")
    private SocialModel social;*/

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

   /* public ResourceModel getResource() {
        return resource;
    }

    public SocialModel getSocial() {
        return social;
    }*/

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

    /*public void setResource(ResourceModel resource) {
        this.resource = resource;
    }

    public void setSocial(SocialModel social) {
        this.social = social;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsFeedModel model = (NewsFeedModel) o;

        if (title != null ? !title.equals(model.title) : model.title != null) return false;
        if (description != null ? !description.equals(model.description) : model.description != null)
            return false;
        if (link != null ? !link.equals(model.link) : model.link != null) return false;
        if (imageUrl != null ? !imageUrl.equals(model.imageUrl) : model.imageUrl != null)
            return false;
        return creatingDate != null ? creatingDate.equals(model.creatingDate) : model.creatingDate == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (creatingDate != null ? creatingDate.hashCode() : 0);
        return result;
    }
}
