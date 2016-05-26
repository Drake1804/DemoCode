package com.drake1804.f1feedler.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.drake1804.f1feedler.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pavel.Shkaran on 5/24/2016.
 */
public class DetailsBottomBar extends LinearLayout {

    private Context context;
    private IController controller;

    @Bind(R.id.text_font_bigger)
    ImageView bigger;

    @Bind(R.id.text_font_smaller)
    ImageView smaller;


    public DetailsBottomBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DetailsBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DetailsBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DetailsBottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public void init(){
        View v = LayoutInflater.from(context).inflate(R.layout.bottom_bar, this, true);
        controller = (IController) context;
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.text_font_smaller)
    public void onSmaller(){
        controller.onFontSmaller();
    }

    @OnClick(R.id.text_font_bigger)
    public void onBigger(){
        controller.onFontBigger();
    }

    @OnClick(R.id.dislike)
    public void onDislike(){
        controller.dislike();
    }

    @OnClick(R.id.like)
    public void onLike(){
        controller.like();
    }

    public interface IController {
        void onFontBigger();
        void onFontSmaller();
        void like();
        void dislike();
    }

    public void setVisibilityButtonBigger(boolean state){
        if(state){
            bigger.setVisibility(VISIBLE);
        } else {
            bigger.setVisibility(GONE);
        }

    }

    public void setVisibilityButtonSmaller(boolean state){
        if(state){
            smaller.setVisibility(VISIBLE);
        } else {
            smaller.setVisibility(GONE);
        }
    }

}
