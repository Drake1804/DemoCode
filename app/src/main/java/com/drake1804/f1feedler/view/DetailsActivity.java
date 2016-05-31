package com.drake1804.f1feedler.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.CommentsAdapter;
import com.drake1804.f1feedler.model.CommentModel;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.utils.AppUtils;
import com.drake1804.f1feedler.view.custom.DetailsBottomBar;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity implements DetailsView, DetailsBottomBar.IController {

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.text)
    TextView text;

    @Bind(R.id.scrollView)
    NestedScrollView scrollView;

    @Bind(R.id.comments)
    RecyclerView comments;

    @Bind(R.id.rate_spinner)
    Spinner rate;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private CommentsAdapter adapter;
    private DetailsPresenter presenter;

    private LinearLayoutManager mLayoutManager;

    private static final int MAX_FONT = 19;
    private static final int MIN_FONT = 12;

    private boolean isHidden = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(getIntent().getStringExtra("title"));
        title.setText(getIntent().getStringExtra("title"));

        presenter = new DetailsPresenter(this);
        presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));
        presenter.getComments(getIntent().getStringExtra("uuid"));

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CommentsAdapter(this);
        comments.setLayoutManager(mLayoutManager);
        comments.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));
                presenter.getComments(getIntent().getStringExtra("uuid"));
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY - oldScrollY > 20 && !isHidden){
                    fab.animate().translationYBy(350).setDuration(500);
                    isHidden = true;
                }
                if(scrollY - oldScrollY < -50 && isHidden){
                    fab.animate().translationYBy(-350);
                    isHidden = false;
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rate, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rate.setAdapter(adapter);

        rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        showMessage("Like");
                        break;
                    case 2:
                        showMessage("Dislike");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showDialog();
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
            case R.id.menu_night:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.menu_font:
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
    public void setComments(List<CommentModel> comments) {
        adapter.setCommentModels(comments);
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
            title.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) + 1);
            text.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) + 1);
            Timber.d("TEXT_SIZE: " + AppUtils.convertPixelsToDp(text.getTextSize(), this));
        }
    }

    @Override
    public void onFontSmaller() {
        if(AppUtils.convertPixelsToDp(text.getTextSize(), this) > MIN_FONT){
            title.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) - 1);
            text.setTextSize(AppUtils.convertPixelsToDp(text.getTextSize(), this) - 1);
            Timber.d("TEXT_SIZE: " + AppUtils.convertPixelsToDp(text.getTextSize(), this));
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

    @OnClick(R.id.read_on_the_web)
    public void readOnTheWeb(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("link")));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
