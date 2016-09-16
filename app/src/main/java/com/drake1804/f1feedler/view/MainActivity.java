package com.drake1804.f1feedler.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.MainFeedAdapter;
import com.drake1804.f1feedler.gcm.RegistrationIntentService;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.presenter.MainFeedPresenter;
import com.drake1804.f1feedler.utils.ItemClickSupport;
import com.drake1804.f1feedler.utils.OfflineMode;
import com.drake1804.f1feedler.view.view.MainFeedView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends BaseActivity implements MainFeedView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mainFeed)
    RecyclerView mainFeed;

    @BindView(R.id.fab)
    FloatingActionButton fabOnTop;

    private MainFeedAdapter adapter;
    private MainFeedPresenter presenter;
    private LinearLayoutManager mLayoutManager;

    public static boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private static int page = 0;

    /*static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        init();
        initListeners();
        showDialog();

        presenter.getNewsFeed(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.registerReceiver();
    }
    @Override
    protected void onPause() {
        super.onPause();
        presenter.unregisterReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OfflineMode offlineMode = new OfflineMode(this, getRealm());
        switch (item.getItemId()){
            case R.id.menu_signIn:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.offline_mode:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                offlineMode.createMode(dialog);
                break;
            case R.id.action_settings:
                break;
            case R.id.offline_mode_clear:
                offlineMode.clearOfflineData(adapter, presenter);
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
    public Realm getRealm() {
        return realm;
    }

    @Override
    public void showDialog() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void dismissDialog() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
    }

    @OnClick(R.id.fab)
    public void onTop(){
        mainFeed.smoothScrollToPosition(0);
    }

    public void init(){
        adapter = new MainFeedAdapter(this);
        presenter = new MainFeedPresenter(this, this);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mainFeed.setLayoutManager(mLayoutManager);
        mainFeed.setAdapter(adapter);

        if (presenter.checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void initListeners(){
        ItemClickSupport.addTo(mainFeed).setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("uuid", adapter.getNewsFeedModels().get(position).getLink());
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });

        mainFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(mLayoutManager.findFirstVisibleItemPosition() > 5 && fabOnTop.getVisibility() == View.GONE){
                    fabOnTop.setVisibility(View.VISIBLE);
                } else if(mLayoutManager.findFirstVisibleItemPosition() < 3 && fabOnTop.getVisibility() == View.VISIBLE){
                    fabOnTop.setVisibility(View.GONE);
                }

                if(dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            page++;
                            presenter.getNewsFeed(page);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> presenter.getNewsFeed(0));
    }

}
