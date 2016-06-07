package com.drake1804.f1feedler.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.CommentsAdapter;
import com.drake1804.f1feedler.model.CommentModel;
import com.drake1804.f1feedler.presenter.DetailsPresenter;
import com.drake1804.f1feedler.utils.AppBarStateChangeListener;
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
    HtmlTextView text;

    @Bind(R.id.scrollView)
    NestedScrollView scrollView;

    @Bind(R.id.comments)
    RecyclerView comments;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.logo)
    ImageView logo;

    @Bind(R.id.resource)
    TextView resource;

    @Bind(R.id.date)
    TextView date;

    @Bind(R.id.facebook_share)
    ShareButton shareButtonFb;

    private CommentsAdapter adapter;
    private DetailsPresenter presenter;
    private LinearLayoutManager mLayoutManager;
    private static boolean isNight = false;
    private CallbackManager callbackManager;


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
                startFontSizeDialog();
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

        callbackManager = CallbackManager.Factory.create();

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

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(getIntent().getStringExtra("link")))
                .setContentTitle(getIntent().getStringExtra("title"))
                .setImageUrl(Uri.parse(getIntent().getStringExtra("imageUrl")))
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

    private void startFontSizeDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(DetailsActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle(getString(R.string.font_size));
        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                DetailsActivity.this,
                R.array.font_size_array,
                android.R.layout.select_dialog_singlechoice);

        builderSingle.setSingleChoiceItems(R.array.font_size_array, Hawk.get(Tweakables.HAWK_KEY_FONT_SIZE, 1), (dialog, which) -> {
            String strName = (String) arrayAdapter.getItem(which);
            Timber.d(strName);
            switch (which){
                case 0:
                    text.setTextSize(12);
                    break;
                case 1:
                    text.setTextSize(14);
                    break;
                case 2:
                    text.setTextSize(16);
                    break;
                case 3:
                    text.setTextSize(18);
                    break;
                case 4:
                    text.setTextSize(20);
                    break;
            }
            dialog.dismiss();
            Hawk.put(Tweakables.HAWK_KEY_FONT_SIZE, which);
        });
        builderSingle.show();
    }
}
