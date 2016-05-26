package com.drake1804.f1feedler.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.utils.AppUtils;
import com.drake1804.f1feedler.view.custom.DetailsBottomBar;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.squareup.picasso.Picasso;

import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity implements DetailsView, DetailsBottomBar.IController {

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.text)
    TextView text;

    @Bind(R.id.bottom_bar)
    DetailsBottomBar bottomBar;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    private DetailsPresenter presenter;

    private int scrollLastPoint;
    private int divScrollUp = 500;
    private int divScrollDown = 500;

    private static final int MAX_FONT = 19;
    private static final int MIN_FONT = 12;


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
        presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));
            }
        });

        showDialog();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_fav:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setData(String imageUrl, String text) {
        dismissDialog();
        try {
            Picasso.with(this).load(imageUrl).into(image);
            this.text.setText(Html.fromHtml(text));
        } catch (Exception e){
            Timber.e(e.getMessage());
        }
        this.text.setText(Html.fromHtml(text));
    }

    @Override
    public void showDialog() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
              swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void dismissDialog() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFontBigger() {
        if(AppUtils.convertPixelsToDp(text.getTextSize(), this) < MAX_FONT){
            bottomBar.setVisibilityButtonBigger(true);
            bottomBar.setVisibilityButtonSmaller(true);
            title.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) + 1);
            text.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) + 1);
            Timber.d("TEXT_SIZE: "+AppUtils.convertPixelsToDp(text.getTextSize(), this));
        }
        if(AppUtils.convertPixelsToDp(text.getTextSize(), this) == MAX_FONT){
            bottomBar.setVisibilityButtonBigger(false);
        }
    }

    @Override
    public void onFontSmaller() {
        if(AppUtils.convertPixelsToDp(text.getTextSize(), this) > MIN_FONT){
            bottomBar.setVisibilityButtonSmaller(true);
            bottomBar.setVisibilityButtonBigger(true);
            title.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) - 1);
            text.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) - 1);
            Timber.d("TEXT_SIZE: "+AppUtils.convertPixelsToDp(text.getTextSize(), this));
        }

        if(AppUtils.convertPixelsToDp(text.getTextSize(), this) == MIN_FONT){
            bottomBar.setVisibilityButtonSmaller(false);
        }
    }

    @Override
    public void like() {
        showMessage("Like");
    }

    @Override
    public void dislike() {
        showMessage("Dislike");
    }
}
