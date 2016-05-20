package com.drake1804.f1feedler.view.view;

import com.drake1804.f1feedler.model.NewsFeedModel;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public interface MainFeedView {

    void showMessage(String message);
    void setData(List<NewsFeedModel> data);
    void showDialog();
    void dismissDialog();
}
