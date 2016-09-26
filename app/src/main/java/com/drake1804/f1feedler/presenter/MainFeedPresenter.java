package com.drake1804.f1feedler.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.gcm.QuickstartPreferences;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.RetrofitException;
import com.drake1804.f1feedler.view.MainActivity;
import com.drake1804.f1feedler.view.view.MainFeedView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedPresenter extends Presenter {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Context context;
    private MainFeedView view;
    private boolean hasNewNews = false;
    private int oldDataSize = 0;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public MainFeedPresenter(Context context, MainFeedView view) {
        this.context = context;
        this.view = view;

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
    }

    public void getNewsFeed(int page){
        List<NewsFeedModel> old = view.getRealm().where(NewsFeedModel.class).findAll();
        oldDataSize = old.size();
        view.setData(old);
        loadFeed(page);
    }

    private void loadFeed(int page){
        RestClient.getInstance().getFeed(page)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsFeedWrapper>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dismissDialog();
                        RetrofitException error = (RetrofitException) e;
                        view.showErrorView(true);

                        if(BuildConfig.DEBUG){
                            view.showMessage(error.getMessage());
                        }
                    }

                    @Override
                    public void onNext(NewsFeedWrapper newsFeedWrapper) {
                        view.showErrorView(false);
                        saveNewsLinks(newsFeedWrapper.embedded.items);
                        view.dismissDialog();
                    }
                });
    }

    public int getOldDataSize() {
        return oldDataSize;
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        view.getRealm().executeTransactionAsync(realm -> {
            List<NewsFeedModel> newsFeedModelListNew = realm.copyToRealmOrUpdate(list);
            if(newsFeedModelListNew.size() != oldDataSize){
                hasNewNews = true;
            }
        },
                () -> {
                    if(hasNewNews){
                        view.setData(view.getRealm().where(NewsFeedModel.class).findAll());
                        hasNewNews = false;
                    }
                },
                Throwable::printStackTrace);
    }

    public void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    public void unregisterReceiver(){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(((MainActivity) context), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.e("This device is not supported.");
                ((MainActivity) context).finish();
            }
            return false;
        }
        return true;
    }
}
