package com.drake1804.f1feedler;

import android.app.Application;

import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Tweakables;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataSourceController.initSingleton(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FlurryAgent.init(this, Tweakables.FLURRY_ANALYTICS_KEY);
    }
}
