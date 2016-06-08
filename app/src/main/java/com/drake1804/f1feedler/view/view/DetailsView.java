package com.drake1804.f1feedler.view.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.drake1804.f1feedler.model.CommentModel;

import java.util.List;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public interface DetailsView extends BaseView {

    void setData(String imageUrl, String text);
    void setComments(List<CommentModel> comments);
    ImageView getLogoView();
    TextView getResource();
    TextView getDate();
    TextView getTextView();
}
