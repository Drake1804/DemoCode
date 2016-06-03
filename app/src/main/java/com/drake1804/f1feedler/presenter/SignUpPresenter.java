package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.SessionModel;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.view.view.SignUpView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public class SignUpPresenter extends Presenter {

    private SignUpView view;

    public SignUpPresenter(SignUpView view) {
        this.view = view;
    }

    public void signUp(String login, String password){
        cleanSession();
        RestClient.getInstance().signUp(login, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SessionModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                        view.onResult(false);
                    }

                    @Override
                    public void onNext(final SessionModel sessionModel) {
                        /*view.getRealm().executeTransactionAsync(realm -> {
                            realm.copyToRealmOrUpdate(sessionModel);
                        }, () -> {
                            view.onResult(true);
                        });*/
                    }
                });
    }

    private void cleanSession(){
        /*view.getRealm().executeTransactionAsync(realm -> {
            RealmResults<SessionModel> sessionModels = realm.where(SessionModel.class)
                    .findAll();
            sessionModels.deleteAllFromRealm();
        });*/
    }

}
