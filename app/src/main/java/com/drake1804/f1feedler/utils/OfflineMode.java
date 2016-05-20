package com.drake1804.f1feedler.utils;

import android.content.Context;
import android.util.Log;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.presenter.MainFeedPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Pavel.Shkaran on 5/20/2016.
 */
public class OfflineMode {

    private static RealmResults<NewsFeedModel> newsFeedModelList;
    private static RealmResults<NewsModel> newsModelList;

    private static RealmChangeListener newsFeedListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    newsFeedModelList.deleteAllFromRealm();
                }
            });
        }
    };

    private static RealmChangeListener newsModelListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    newsModelList.deleteAllFromRealm();
                }
            });
        }
    };


    public static void offlineMode(List<NewsFeedModel> newsFeedModels, MainFeedPresenter presenter){
        for(NewsFeedModel model : newsFeedModels){
            presenter.loadForOfflineMode(model.getLink());
            Log.d("OFFLINE_MODE", model.getLink());
        }
    }

    public static void clearOfflineCache(Context context){ //TODO doesn't work correct
        Realm realm = Realm.getDefaultInstance();
        newsFeedModelList = realm.where(NewsFeedModel.class).findAllAsync();
        newsModelList = realm.where(NewsModel.class).findAllAsync();

        newsFeedModelList.addChangeListener(newsFeedListener);
        newsModelList.addChangeListener(newsModelListener);

        PicassoTools.clearCache(Picasso.with(context));

    }

}
