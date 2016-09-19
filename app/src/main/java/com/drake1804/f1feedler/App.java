package com.drake1804.f1feedler;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Tweakables;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.Hawk;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        DataSourceController.initSingleton(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FlurryAgent.init(this, Tweakables.FLURRY_ANALYTICS_KEY);

        MobileAds.initialize(getApplicationContext(), Tweakables.ADS_APP_ID);

    }
}
