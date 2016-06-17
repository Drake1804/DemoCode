package com.drake1804.f1feedler.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.presenter.LoginPresenter;
import com.drake1804.f1feedler.view.view.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * A signIn screen that offers signIn via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.email)
    TextInputEditText login;

    @BindView(R.id.password)
    TextInputEditText password;

    private ProgressDialog progress;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        int width = display.widthPixels;
        int height = display.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int) (height*0.7));

        presenter = new LoginPresenter(this);
        initDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }

    @OnClick(R.id.email_sign_in_button)
    public void onSignIn(){
        if(isFieldsValid()){
            presenter.signIn(login.getText().toString(), password.getText().toString());
        }
    }

    @OnClick(R.id.register_button)
    public void onRegisterButton(){
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();
    }

    private void initDialog(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setMessage(getString(R.string.loading));
    }

    public boolean isFieldsValid() {
        if(TextUtils.isEmpty(login.getText().toString())){
            showMessage(getString(R.string.empty_login));
            return false;
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            showMessage(getString(R.string.empty_password));
            return false;
        }
        return true;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public Realm getRealm() {
        return realm;
    }

    @Override
    public void showDialog() {
        progress.show();
    }

    @Override
    public void dismissDialog() {
        progress.dismiss();
    }

    @Override
    public void onResult(boolean result) {
        if(result){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}

