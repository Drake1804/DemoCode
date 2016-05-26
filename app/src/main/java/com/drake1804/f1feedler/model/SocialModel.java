package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Pavel.Shkaran on 5/26/2016.
 */
public class SocialModel extends RealmObject {

    @SerializedName("views")
    private int views;

    @SerializedName("voting_plus")
    private int likes;

    @SerializedName("voting_minus")
    private int dislikes;

    @SerializedName("favorites")
    private int favorites;

    @SerializedName("comments")
    private int comments;



    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public int getFavorites() {
        return favorites;
    }

    public int getComments() {
        return comments;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
