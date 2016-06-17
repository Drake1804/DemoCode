package com.drake1804.f1feedler.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.presenter.IntroPresenter;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.BaseView;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.orhanobut.hawk.Hawk;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroActivity extends AppIntro2 implements BaseView {

    private Realm realm;
    private IntroPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Hawk.contains(Tweakables.HAWK_KEY_IS_FIRST_LAUNCH)){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Hawk.put(Tweakables.HAWK_KEY_IS_FIRST_LAUNCH, true);
        init();
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public Realm getRealm() {
        return realm;
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void showMessage(String message) {

    }

    private void init(){
        realm = Realm.getDefaultInstance();
        presenter = new IntroPresenter(this);
        presenter.loadFeed();

        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), getString(R.string.news_app), R.mipmap.ic_launcher, getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.smart_news), getString(R.string.get_smart_news), R.mipmap.ic_launcher, getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.gamification), getString(R.string.news_reading_fun), R.mipmap.ic_launcher, getColor(R.color.colorPrimary)));
    }
}
