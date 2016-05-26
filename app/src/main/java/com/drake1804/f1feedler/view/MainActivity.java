package com.drake1804.f1feedler.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.MainFeedAdapter;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.presenter.MainFeedPresenter;
import com.drake1804.f1feedler.utils.ItemClickSupport;
import com.drake1804.f1feedler.utils.OfflineMode;
import com.drake1804.f1feedler.view.view.MainFeedView;

import java.util.List;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFeedView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.mainFeed)
    RecyclerView mainFeed;

    private MainFeedAdapter adapter;
    private MainFeedPresenter presenter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        adapter = new MainFeedAdapter(this);
        presenter = new MainFeedPresenter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mainFeed.setLayoutManager(mLayoutManager);
        mainFeed.setAdapter(adapter);

        showDialog();

        ItemClickSupport.addTo(mainFeed).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", adapter.getNewsFeedModels().get(position).getTitle());
                intent.putExtra("link", adapter.getNewsFeedModels().get(position).getLink());
                intent.putExtra("imageUrl", adapter.getNewsFeedModels().get(position).getImageUrl());
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getNewsFeed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getNewsFeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_signIn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.offline_mode:
                OfflineMode.createMode();
                break;
            case R.id.action_settings:
                break;
            case R.id.offline_mode_clear:
//                OfflineMode.clearOfflineData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setData(List<NewsFeedModel> data) {
        adapter.setData(data);
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
