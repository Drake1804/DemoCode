package com.drake1804.f1feedler.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.drake1804.f1feedler.R;

import butterknife.ButterKnife;

/**
 * Created by Pavel.Shkaran on 5/27/2016.
 */
public class LabelView extends LinearLayout {

    private Context context;

    public LabelView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init(){
        View v = LayoutInflater.from(context).inflate(R.layout.label_view, this, true);
        ButterKnife.bind(this, v);
    }


}
