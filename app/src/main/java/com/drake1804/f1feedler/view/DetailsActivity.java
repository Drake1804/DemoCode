package com.drake1804.f1feedler.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.CommentsAdapter;
import com.drake1804.f1feedler.model.CommentModel;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.utils.AppBarStateChangeListener;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import timber.log.Timber;

public class DetailsActivity extends BaseActivity implements DetailsView {

    @Bind(R.id.app_bar)
    AppBarLayout appBar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

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

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.logo)
    ImageView logo;

    @Bind(R.id.resource)
    TextView recource;

    @Bind(R.id.date)
    TextView date;

    private CommentsAdapter adapter;
    private DetailsPresenter presenter;
    private LinearLayoutManager mLayoutManager;
    private boolean isNight = false;
    private boolean isVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        initListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(isNight);
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
                item.setChecked(isNight);
                if(isNight){
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    isNight = false;
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    isNight = true;
                }
                Hawk.put(Tweakables.HAWK_KEY_NIGHT_MODE, isNight);
                recreate();
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
    public ImageView getLogoView() {
        return logo;
    }

    @Override
    public TextView getResource() {
        return recource;
    }

    @Override
    public TextView getDate() {
        return date;
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

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.read_on_the_web)
    public void readOnTheWeb(){
        new CustomTabsIntent.Builder()
                .setToolbarColor(getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse(getIntent().getStringExtra("link")));
    }

    private void init(){
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra("title"));
        title.setText(getIntent().getStringExtra("title"));

        presenter = new DetailsPresenter(this, this);
        presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));
        presenter.getComments(getIntent().getStringExtra("uuid"));
        presenter.setHeader(getIntent().getStringExtra("logoUrl"), getIntent().getStringExtra("resource"), getIntent().getLongExtra("date", 0));

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CommentsAdapter(this);
        comments.setLayoutManager(mLayoutManager);
        comments.setAdapter(adapter);

        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state){
                    case EXPANDED:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case COLLAPSED:
                        fab.setVisibility(View.GONE);
                        break;
                }
            }
        });

        if(Hawk.contains(Tweakables.HAWK_KEY_NIGHT_MODE)){
            isNight = Hawk.get(Tweakables.HAWK_KEY_NIGHT_MODE);
        }
    }

    private void initListeners(){
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.getPage(getIntent().getStringExtra("link"), getIntent().getStringExtra("imageUrl"));
            presenter.getComments(getIntent().getStringExtra("uuid"));
        });
    }
}
