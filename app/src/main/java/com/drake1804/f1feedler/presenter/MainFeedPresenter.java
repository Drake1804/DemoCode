package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.view.view.MainFeedView;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedPresenter extends Presenter {

    private MainFeedView view;
    private boolean hasNewNews = false;
    private int oldDataSize = 0;

    public void getNewsFeed(){
        List<NewsFeedModel> old = view.getRealm().where(NewsFeedModel.class).findAll();
        oldDataSize = old.size();
        view.setData(old);
        loadFeed();
    }

    public MainFeedPresenter(MainFeedView view) {
        this.view = view;
    }

    public void loadFeed(){
        RestClient.getInstance().getFeed()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsFeedWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                        view.dismissDialog();
                    }

                    @Override
                    public void onNext(NewsFeedWrapper newsFeedWrapper) {
                        saveNewsLinks(newsFeedWrapper.items);
                        view.dismissDialog();
                    }
                });
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
}
