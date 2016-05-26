package com.drake1804.f1feedler.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.Source;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.presenter.MainFeedPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Pavel.Shkaran on 5/20/2016.
 */
public class OfflineMode {

    private static Context context;
    private static Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public OfflineMode(Context context) {
        this.context = context;
    }

    public static void createMode(){
        RestClient.getInstance().getFeed()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsFeedWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(NewsFeedWrapper newsFeedWrapper) {
                        saveNewsLinks(newsFeedWrapper.items);
                    }
                });
    }

    private static void saveNewsLinks(final List<NewsFeedModel> list){
        DataSourceController.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                for(NewsFeedModel model : list){
                    loadForOfflineNews(model.getLink(), model.getImageUrl());
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    public static void parseNews(final String url, @Nullable final String imageUrl){
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {
                Document document;
                try {
                    document = Jsoup.connect(url).get();
                    Timber.d("Loaded: "+url);
                    subscriber.onNext(document);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Document>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Document document) {
                        if(url.contains(Tweakables.F1NEWS)){
                            Elements head = document.getElementsByAttributeValue("class", "post_head");
                            Elements body = document.getElementsByAttributeValue("class", "post_body");

                            savePage(url, head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"), body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                        } else if(url.contains(Tweakables.F1WORLD)){

                        } else if(url.contains(Tweakables.CHAMPIONAT)){

                        } else if(url.contains(Tweakables.AUTOSPORT)){
                            Elements top = document.getElementsByAttributeValue("id", "story");
                            Elements article = top.get(0).getElementsByAttributeValue("class", "field-item even");

                            savePage(url, imageUrl, article.html());
                        }
                    }
                });
    }

    private static void savePage(final String url, final String imageUrl, final String text){
        Picasso.with(context)
                .load(imageUrl)
                .into(target);
        DataSourceController.getRealm().beginTransaction();
        NewsModel newsModel = new NewsModel();
        newsModel.setUrl(url);
        newsModel.setImageUrl(imageUrl);
        newsModel.setText(text);
        DataSourceController.getRealm().copyToRealmOrUpdate(newsModel);
        DataSourceController.getRealm().commitTransaction();
    }

    private static void loadForOfflineNews(String url, String imageUrl){
        NewsModel newsModel = DataSourceController.getRealm().where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parseNews(url, imageUrl);
        }
    }

    @Deprecated
    public static void clearOfflineData(){ //TODO rewrite
        final RealmResults<NewsFeedModel> feedModelRealmResults = DataSourceController.getRealm().where(NewsFeedModel.class).findAll();
        final RealmResults<NewsModel> newsModelRealmResults = DataSourceController.getRealm().where(NewsModel.class).findAll();

        DataSourceController.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                feedModelRealmResults.deleteAllFromRealm();
                newsModelRealmResults.deleteAllFromRealm();
                Timber.d("Offline data was cleared!");
            }
        });

    }

}
