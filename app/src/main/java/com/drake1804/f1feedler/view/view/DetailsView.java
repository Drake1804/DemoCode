package com.drake1804.f1feedler.view.view;

import com.drake1804.f1feedler.model.CommentModel;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public interface DetailsView {

    void setData(String imageUrl, String text);
    void setComments(List<CommentModel> comments);
    void showDialog();
    void dismissDialog();
    void showMessage(String message);
}
