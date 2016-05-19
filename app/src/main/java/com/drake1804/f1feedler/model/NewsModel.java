package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class NewsModel extends RealmObject {

    @PrimaryKey
    @SerializedName("url")
    private String url;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("text")
    private String text;

    @SerializedName("likes")
    private LikesModel likesModel;

    @Ignore
    @SerializedName("comments")
    private List<CommentModel> commentModelList;


    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public LikesModel getLikesModel() {
        return likesModel;
    }

    public List<CommentModel> getCommentModelList() {
        return commentModelList;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLikesModel(LikesModel likesModel) {
        this.likesModel = likesModel;
    }

    public void setCommentModelList(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }
}
