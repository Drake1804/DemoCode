package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/30/2016.
 */
public class CommentsWrapper {


    @SerializedName("comments")
    public List<CommentModel> comments;

}
