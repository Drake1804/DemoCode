package com.drake1804.f1feedler.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.presenter.LoginPresenter;
import com.drake1804.f1feedler.utils.AppUtils;
import com.drake1804.f1feedler.view.view.LoginView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginView {

    @Bind(R.id.email)
    TextInputEditText login;

    @Bind(R.id.password)
    TextInputEditText password;

    private ProgressDialog progress;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter = new LoginPresenter(this);
        initDialog();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideDialog();
    }

    @OnClick(R.id.email_sign_in_button)
    public void onSignIn(){
        if(isFieldsValid()){
            presenter.login(login.getText().toString(), password.getText().toString());
        }
    }

    private void initDialog(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setMessage("Loading...");
    }

    public boolean isFieldsValid() {
        if(TextUtils.isEmpty(login.getText().toString())){
            showMessage("Empty login");
            return false;
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            showMessage("Empty password");
            return false;
        }
        return true;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog() {
        progress.show();

    }

    @Override
    public void hideDialog() {
        progress.dismiss();
    }
}

