package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/26/2016.
 */
public class NewsFeedWrapper {

    @SerializedName("news")
    public List<NewsFeedModel> items;
}
