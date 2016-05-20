package com.drake1804.f1feedler.view.view;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public interface LoginView {

    void showMessage(String message);
    void showDialog();
    void hideDialog();
    void onResult(boolean result);

}
