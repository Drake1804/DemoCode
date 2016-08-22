package com.drake1804.f1feedler.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.CommentsAdapter;
import com.drake1804.f1feedler.model.CommentModel;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.ClickableTableSpan;
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import timber.log.Timber;

public class DetailsActivity extends BaseActivity implements DetailsView {

    @BindView(R.id.app_bar)
    AppBarLayout appBar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.text)
    HtmlTextView text;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.comments)
    RecyclerView comments;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.resource)
    TextView resource;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.facebook_share)
    ShareButton shareButtonFb;

    private CommentsAdapter adapter;
    private DetailsPresenter presenter;
    private LinearLayoutManager mLayoutManager;
    private static boolean isNight = false;
    private CallbackManager callbackManager;
    private NewsFeedModel currentNews;


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
        if(isNight){
            menuItem.setChecked(true);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            menuItem.setChecked(false);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
                if(Hawk.get(Tweakables.HAWK_KEY_NIGHT_MODE)){
                    isNight = false;
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    isNight = true;
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                Hawk.put(Tweakables.HAWK_KEY_NIGHT_MODE, isNight);
                recreate();
                return true;
            case R.id.menu_font:
                presenter.startFontSizeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setData(String imageUrl, String text1) {
        dismissDialog();
        try {
            Picasso.with(this).load(imageUrl).into(image);
        } catch (Exception e){
            Timber.e(e.getMessage());
        }
        text.setRemoveFromHtmlSpace(true);
        text.setClickableTableSpan(new ClickableTableSpanImpl());
        DrawTableLinkSpan drawTableLinkSpan = new DrawTableLinkSpan();
        drawTableLinkSpan.setTableLinkText(getString(R.string.show_table));
        text.setDrawTableLinkSpan(drawTableLinkSpan);
        text.setHtmlFromString(text1, new HtmlTextView.RemoteImageGetter());
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
        return resource;
    }

    @Override
    public TextView getDate() {
        return date;
    }

    @Override
    public TextView getTextView() {
        return text;
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
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse(currentNews.getLink()));
    }

    @OnClick(R.id.fab)
    public void onRateClick(){
        startActivity(new Intent(this, RatePopupActivity.class));
    }

    @OnClick(R.id.facebook_share)
    public void onShare(){
        shareButtonFb.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void init(){
        presenter = new DetailsPresenter(this, this);
        currentNews = realm.where(NewsFeedModel.class).equalTo("link", getIntent().getStringExtra("uuid")).findFirst();

        collapsingToolbarLayout.setTitle(currentNews.getTitle());
        title.setText(currentNews.getTitle());

        presenter.getPage(currentNews.getLink(), currentNews.getImageUrl());
//        presenter.getComments(currentNews.getId());
//        presenter.setHeader(currentNews.getResource().getImageUrl(), currentNews.getResource().getTitle(), currentNews.getCreatingDate() != null ? currentNews.getCreatingDate().getTime() : 0);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CommentsAdapter(this);
        comments.setLayoutManager(mLayoutManager);
        comments.setAdapter(adapter);

        callbackManager = CallbackManager.Factory.create();

        if(Hawk.contains(Tweakables.HAWK_KEY_NIGHT_MODE)){
            isNight = Hawk.get(Tweakables.HAWK_KEY_NIGHT_MODE);
        }
    }

    private void initListeners(){
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.getPage(currentNews.getLink(), currentNews.getImageUrl());
//            presenter.getComments(currentNews.getId());
        });

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(currentNews.getLink()))
                .setContentTitle(currentNews.getTitle())
                .setImageUrl(Uri.parse(currentNews.getImageUrl()))
                .setContentDescription(getString(R.string.share_credentials_fb))
                .build();
        shareButtonFb.setShareContent(content);
    }

    class ClickableTableSpanImpl extends ClickableTableSpan {
        @Override
        public ClickableTableSpan newInstance() {
            return new ClickableTableSpanImpl();
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(DetailsActivity.this, WebActivity.class);
            intent.putExtra(WebActivity.EXTRA_TABLE_HTML, getTableHtml());
            startActivity(intent);
        }
    }
}
