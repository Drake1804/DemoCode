package com.drake1804.f1feedler.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
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


    public NewsFeedView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public NewsFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NewsFeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }


    private void init(){
        View v = LayoutInflater.from(context).inflate(R.layout.main_feed_card, this, true);
        ButterKnife.bind(this, v);
    }

    public void setData(NewsFeedModel model){
        if(!TextUtils.equals(model.getImageUrl(), "")){
            Picasso.with(context)
                    .load(model.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(image);
        }
        title.setText(model.getTitle());
        date.setText(model.getCreatingDate());
        description.setText(model.getDescription());
        resource.setText(model.getResource());
    }
}
