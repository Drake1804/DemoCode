package com.drake1804.f1feedler.utils;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Pavel.Shkaran on 5/23/2016.
 */
public class DataSourceController {

    private static DataSourceController sInstance;
    private static Realm realm;

    private DataSourceController(Context context) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }

    public static synchronized DataSourceController getInstance() {
        return sInstance;
    }


    public static DataSourceController initSingleton(Context context) {
        if (sInstance == null) {
            sInstance = new DataSourceController(context);
        }
        return sInstance;
    }

    public static Realm getRealm() {
        return realm;
    }
}
