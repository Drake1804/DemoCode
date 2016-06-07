package com.drake1804.f1feedler.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.drake1804.f1feedler.R;

import butterknife.Bind;

/**
 * Created by Pavel.Shkaran on 6/7/2016.
 */
public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_TABLE_HTML = "EXTRA_TABLE_HTML";

    @Bind(R.id.web_view)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);
        String tableHtml = getIntent().getStringExtra(EXTRA_TABLE_HTML);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        mWebView.loadDataWithBaseURL(null, tableHtml, "text/html", "utf-8", null);
    }

}
