package com.drake1804.f1feedler.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.utils.Tweakables;
import com.orhanobut.hawk.Hawk;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Pavel.Shkaran on 6/7/2016.
 */
public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_TABLE_HTML = "EXTRA_TABLE_HTML";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.web_view)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String tableHtml = getIntent().getStringExtra(EXTRA_TABLE_HTML);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        mWebView.loadDataWithBaseURL(null, tableHtml, "text/html", "utf-8", null);
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

}
