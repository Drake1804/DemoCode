package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.SessionModel;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.view.view.LoginView;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class LoginPresenter extends Presenter {

    private LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    public void signIn(String login, String password){
        cleanSession();
        RestClient.getInstance().signIn(login, password)
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
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(sessionModel);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                view.onResult(true);
                            }
                        });
                    }
                });
    }

    private void cleanSession(){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SessionModel> sessionModels = realm.where(SessionModel.class)
                        .findAll();
                sessionModels.deleteAllFromRealm();
            }
        });
    }

}
