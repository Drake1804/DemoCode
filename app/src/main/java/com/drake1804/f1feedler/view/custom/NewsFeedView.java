package com.drake1804.f1feedler.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.utils.TimeAgo;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public class NewsFeedView extends LinearLayout {

    private Context context;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.date)
    TextView date;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.resource)
    TextView resource;

    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.dot_progress_bar)
    DotProgressBar dotProgressBar;


    public NewsFeedView(Context context, boolean isMainNews) {
        super(context);
        this.context = context;
        init(isMainNews);
    }

    public NewsFeedView(Context context, AttributeSet attrs, boolean isMainNews) {
        super(context, attrs);
        this.context = context;
        init(isMainNews);
    }

    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr, boolean isMainNews) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(isMainNews);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, boolean isMainNews) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(isMainNews);
    }


    private void init(boolean isMainNews){
        View v;
        if(isMainNews){
            v = LayoutInflater.from(context).inflate(R.layout.top_news_card, this, true);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.main_feed_card, this, true);
        }
        ButterKnife.bind(this, v);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dotProgressBar.setVisibility(VISIBLE);
                dotProgressBar.setStartColor(getContext().getColor(R.color.colorPrimary));
                dotProgressBar.setEndColor(getContext().getColor(R.color.colorAccent));
                dotProgressBar.setDotAmount(5);
                dotProgressBar.setAnimationTime(500);
            }
        }, 700);
    }

    public void setData(NewsFeedModel model){
        if(!TextUtils.equals(model.getImageUrl(), "")){
            Picasso.with(context)
                    .load(model.getImageUrl())
                    .into(image);
        }
        if(model.getResource() != null){
            Picasso.with(context)
                    .load(model.getResource().getImageUrl())
                    .into(icon);
            resource.setText(model.getResource().getTitle());
        }
        title.setText(model.getTitle());
        date.setText(TimeAgo.toDuration(System.currentTimeMillis() - model.getCreatingDate().getTime()));
        description.setText(Html.fromHtml(model.getDescription()));
    }
}
