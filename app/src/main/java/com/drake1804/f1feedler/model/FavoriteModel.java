package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class FavoriteModel extends RealmObject {

    @SerializedName("userId")
    private String userId;

    @Ignore
    @SerializedName("favoriteNews")
    private List<NewsModel> newsModelList;

}
