package com.drake1804.f1feedler;

import android.app.Application;

import com.drake1804.f1feedler.utils.DataSourceController;
import com.squareup.picasso.Picasso;

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

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());

            Picasso.with(getApplicationContext())
                    .setIndicatorsEnabled(true);

//            Picasso.with(getApplicationContext())
//                    .setLoggingEnabled(true);
        }
    }
}
