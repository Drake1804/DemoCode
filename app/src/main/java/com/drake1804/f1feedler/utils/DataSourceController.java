package com.drake1804.f1feedler.utils;

import android.content.Context;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.R;
import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Pavel.Shkaran on 5/23/2016.
 */
public class DataSourceController {

    private static DataSourceController sInstance;
    private static RxBus rxBus;

    private DataSourceController(Context context) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        Hawk.init(context)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.FULL)
                .build();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());

            Picasso.with(context)
                    .setIndicatorsEnabled(true);
            Picasso.with(context)
                    .setLoggingEnabled(true);

            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                            .build());

            RealmInspectorModulesProvider.builder(context)
                    .withFolder(context.getCacheDir())
                    .withEncryptionKey("encrypted.realm", "keyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy".getBytes())
                    .withMetaTables()
                    .withDescendingOrder()
                    .withLimit(1000)
                    .databaseNamePattern(Pattern.compile(".+\\.realm"))
                    .build();

            rxBus = new RxBus();
        }

    }

    public static synchronized DataSourceController getInstance() {
        return sInstance;
    }

    public static RxBus getRxBus() {
        return rxBus;
    }

    public static DataSourceController initSingleton(Context context) {
        if (sInstance == null) {
            sInstance = new DataSourceController(context);
        }
        return sInstance;
    }
}
