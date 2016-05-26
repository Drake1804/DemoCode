package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.view.view.MainFeedView;

import java.util.List;

import io.realm.Realm;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedPresenter extends Presenter {

    private MainFeedView view;

    public void getNewsFeed(){
        view.setData(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
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
        DataSourceController.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                view.setData(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
