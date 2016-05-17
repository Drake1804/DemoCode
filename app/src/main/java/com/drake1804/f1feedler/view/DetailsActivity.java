package com.drake1804.f1feedler.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
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

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.text)
    TextView text;

    private DetailsPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra("title"));

        presenter = new DetailsPresenter(this);

        presenter.getPage(getIntent().getStringExtra("link"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getPage(getIntent().getStringExtra("link"));
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
