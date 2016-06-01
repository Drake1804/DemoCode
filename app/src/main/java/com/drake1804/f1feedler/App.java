package com.drake1804.f1feedler;

import android.app.Application;

import com.drake1804.f1feedler.utils.DataSourceController;
import com.facebook.stetho.Stetho;
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

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());

            Picasso.with(getApplicationContext())
                    .setIndicatorsEnabled(true);

//            Picasso.with(getApplicationContext())
//                    .setLoggingEnabled(true);

            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());

            RealmInspectorModulesProvider.builder(this)
                    .withFolder(getCacheDir())
                    .withEncryptionKey("encrypted.realm", "keyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy".getBytes())
                    .withMetaTables()
                    .withDescendingOrder()
                    .withLimit(1000)
                    .databaseNamePattern(Pattern.compile(".+\\.realm"))
                    .build();
        }
    }
}
