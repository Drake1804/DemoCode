package com.drake1804.f1feedler.utils;

import android.content.Context;

import com.drake1804.f1feedler.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

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

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
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
