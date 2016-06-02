package com.drake1804.f1feedler.view.view;

import io.realm.Realm;

/**
 * Created by Pavel.Shkaran on 6/2/2016.
 */
public interface BaseView {

    Realm getRealm();
    void showDialog();
    void dismissDialog();
    void showMessage(String message);
}
