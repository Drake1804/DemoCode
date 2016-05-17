package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class LikesModel extends RealmObject {

    @PrimaryKey
    @SerializedName("newsId")
    private String newsId;

    @SerializedName("count")
    private int count;

    @Ignore
    @SerializedName("users")
    private List<UserModel> userModelList;


    public String getNewsId() {
        return newsId;
    }

    public int getCount() {
        return count;
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }
}
