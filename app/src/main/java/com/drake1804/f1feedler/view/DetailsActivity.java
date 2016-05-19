package com.drake1804.f1feedler.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.squareup.picasso.Picasso;

import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements DetailsView {

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.text)
    TextView text;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Bind(R.id.bottom_bar)
    LinearLayout bottomBar;

    private DetailsPresenter presenter;

    private int scrollLastPoint;
    private int divScrollUp = 500;
    private int divScrollDown = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra("title"));
        title.setText(getIntent().getStringExtra("title"));

        presenter = new DetailsPresenter(this);

        presenter.getPage(getIntent().getStringExtra("link"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getPage(getIntent().getStringExtra("link"));
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if(scrollY == 0) swipeRefreshLayout.setEnabled(true);
                else swipeRefreshLayout.setEnabled(false);
                if(scrollLastPoint == 0){
                    scrollLastPoint = scrollY;
                } else if(scrollY - scrollLastPoint > divScrollDown) {
                    bottomBar.setVisibility(View.GONE);
                } else if(scrollY - scrollLastPoint < divScrollUp) {
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setData(String imageUrl, String text) {
        Picasso.with(this).load(imageUrl).into(image);
        this.text.setText(Html.fromHtml(text));
    }

    @Override
    public void showDialog() {
        swipeRefreshLayout.post(new TimerTask() {
            @Override
            public void run() {
              swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void dismissDialog() {
        swipeRefreshLayout.post(new TimerTask() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
