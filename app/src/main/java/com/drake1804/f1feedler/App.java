package com.drake1804.f1feedler;

import android.app.Application;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        Picasso
                .with(getApplicationContext())
                .setIndicatorsEnabled(true);

        Picasso
                .with(getApplicationContext())
                .setLoggingEnabled(true);
    }
}
