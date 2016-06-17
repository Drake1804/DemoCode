package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.view.view.BaseView;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Pavel.Shkaran on 6/17/2016.
 */
public class IntroPresenter extends Presenter {

    private BaseView view;

    public IntroPresenter(BaseView view) {
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
                        if(BuildConfig.DEBUG){
                            Timber.e(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(NewsFeedWrapper newsFeedWrapper) {
                        saveNewsLinks(newsFeedWrapper.items);
                    }
                });
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        view.getRealm().executeTransactionAsync(realm -> {
                    realm.copyToRealmOrUpdate(list);
                },
                Throwable::printStackTrace);
    }

}
