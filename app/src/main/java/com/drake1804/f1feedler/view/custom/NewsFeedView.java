package com.drake1804.f1feedler.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.utils.TimeAgo;
import com.drake1804.f1feedler.view.RatePopupActivity;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public class NewsFeedView extends LinearLayout {

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.resource)
    TextView resource;

    @BindView(R.id.icon)
    ImageView icon;

    @BindView(R.id.dot_progress_bar)
    DotProgressBar dotProgressBar;

    @BindView(R.id.menu)
    ImageButton menu;

    @BindView(R.id.likes)
    TextView likes;

    @BindView(R.id.comments)
    TextView comments;


    public NewsFeedView(Context context, boolean isMainNews) {
        super(context);
        init(isMainNews);
    }

    public NewsFeedView(Context context, AttributeSet attrs, boolean isMainNews) {
        super(context, attrs);
        init(isMainNews);
    }

    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr, boolean isMainNews) {
        super(context, attrs, defStyleAttr);
        init(isMainNews);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, boolean isMainNews) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(isMainNews);
    }

    @OnClick(R.id.menu)
    public void onPopup(){
        getContext().startActivity(new Intent(getContext(), RatePopupActivity.class));
    }


    private void init(boolean isMainNews){
        View v;
        if(isMainNews){
            v = LayoutInflater.from(getContext()).inflate(R.layout.top_news_card, this, true);
        } else {
            v = LayoutInflater.from(getContext()).inflate(R.layout.main_feed_card, this, true);
        }
        ButterKnife.bind(this, v);
        new Handler().postDelayed(() -> {
            dotProgressBar.setVisibility(VISIBLE);
            dotProgressBar.setStartColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            dotProgressBar.setEndColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            dotProgressBar.setDotAmount(5);
            dotProgressBar.setAnimationTime(500);
        }, 700);
    }

    public void setData(NewsFeedModel model){
        if(!TextUtils.equals(model.getImageUrl(), "")){
            Picasso.with(getContext())
                    .load(model.getImageUrl())
                    .into(image);
        }
        /*if(model.getResource() != null){
            Picasso.with(getContext())
                    .load(model.getResource().getImageUrl())
                    .into(icon);
            resource.setText(model.getResource().getTitle());
        }*/
        title.setText(model.getTitle());
        date.setText(TimeAgo.toDuration(System.currentTimeMillis() - model.getCreatingDate().getTime()));
        description.setText(Html.fromHtml(model.getDescription()));
        /*likes.setText(model.getSocial().getLikes()+"");
        comments.setText(model.getSocial().getComments()+"");*/
    }
}
