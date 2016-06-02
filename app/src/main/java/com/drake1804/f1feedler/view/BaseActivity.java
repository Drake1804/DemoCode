package com.drake1804.f1feedler.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.drake1804.f1feedler.view.view.BaseView;

import io.realm.Realm;

/**
 * Created by Pavel.Shkaran on 6/2/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
