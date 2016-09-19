package com.drake1804.f1feedler.view.view;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.utils.EndlessRecyclerOnScrollListener;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public interface MainFeedView extends BaseView {

    void setData(List<NewsFeedModel> data);

    void showErrorView(boolean state);

    EndlessRecyclerOnScrollListener getEndlessRecyclerOnScrollListener();
}
