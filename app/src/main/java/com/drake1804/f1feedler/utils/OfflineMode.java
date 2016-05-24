package com.drake1804.f1feedler.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.Source;
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

    private static Parser parser;
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
        this.parser = new Parser((Parser.IOnData) context);
        this.context = context;
    }

    public static void createMode(){
        Observable.create(new Observable.OnSubscribe<List<Source>>() {
            @Override
            public void call(Subscriber<? super List<Source>> subscriber) {
                List<Source> sources = new ArrayList<>();
                sources.add(new Source(Tweakables.F1NEWS, Tweakables.F1NEWS_FEED_URL));
                sources.add(new Source(Tweakables.CHAMPIONAT, Tweakables.CHAMPIONAT_FEED_URL));
                sources.add(new Source(Tweakables.F1WORLD, Tweakables.F1WORLD_FEED_URL));
                OkHttpClient client = new OkHttpClient();

                for(Source source : sources){
                    Request request = new Request.Builder()
                            .url(source.getUrl())
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        Timber.d("Loaded: "+source.getUrl());
                        source.setDocument(Jsoup.parse(response.body().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
                subscriber.onNext(sources);
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Source>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Source> sources) {
                        for(Source s : sources){
                            switch (s.getName()){
                                case Tweakables.F1NEWS:
                                    List<NewsFeedModel> newsFeedModels = new ArrayList<>();
                                    try {
                                        Elements topLevel = s.getDocument().getElementsByTag("item");

                                        for(Element element : topLevel){
                                            NewsFeedModel newsFeedModel = new NewsFeedModel();
                                            newsFeedModel.setTitle(element.getElementsByTag("title").text());
                                            newsFeedModel.setDescription(element.getElementsByTag("description").text());
                                            newsFeedModel.setLink(element.getElementsByTag("link").text());
                                            if(TextUtils.equals(newsFeedModel.getLink(), "")){
                                                newsFeedModel.setLink(element.getElementsByTag("guid").text());
                                            }
                                            newsFeedModel.setCreatingDate(element.getElementsByTag("pubDate").text());
                                            if(element.getElementsByTag("enclosure").size() > 0){
                                                newsFeedModel.setImageUrl(element.getElementsByTag("enclosure").get(0).attributes().get("url"));
                                            }
                                            newsFeedModels.add(newsFeedModel);
                                        }
                                        saveNewsLinks(newsFeedModels);
                                    } catch (Exception ignored){
                                        ignored.printStackTrace();
                                    }
                                    break;
                                case Tweakables.F1WORLD:
                                    break;
                                case Tweakables.CHAMPIONAT:
                                    break;
                            }
                        }
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
                    loadForOfflineNews(model.getLink());
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    public static void parseNews(final String url){
        if(url.contains(Tweakables.F1NEWS)){
            Observable.create(new Observable.OnSubscribe<Document>() {
                @Override
                public void call(Subscriber<? super Document> subscriber) {
                    Document document;
                    try {
                        document = Jsoup.connect(url).get();
                        Timber.d("Loaded: "+url);
                        subscriber.onNext(document);
                    } catch (IOException e) {
//                        iOnData.onError(e.getLocalizedMessage());
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
//                            iOnData.onError(e.getMessage());
                        }

                        @Override
                        public void onNext(Document document) {
                            Elements head = document.getElementsByAttributeValue("class", "post_head");
                            Elements body = document.getElementsByAttributeValue("class", "post_body");

                            savePage(url, head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"), body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                            Timber.d("PAGE: "+url);
                        }
                    });
        } else if(url.contains(Tweakables.F1WORLD)){

        } else if(url.contains(Tweakables.CHAMPIONAT)){

        }
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

//        iOnData.onDataDetails(newsModel.getImageUrl(), newsModel.getText());
    }

    private static void loadForOfflineNews(String url){
        NewsModel newsModel = DataSourceController.getRealm().where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parseNews(url);
        }
    }

    /*private static RealmResults<NewsFeedModel> newsFeedModelList;
    private static RealmResults<NewsModel> newsModelList;

    private static RealmChangeListener newsFeedListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    newsFeedModelList.deleteAllFromRealm();
                }
            });
        }
    };

    private static RealmChangeListener newsModelListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    newsModelList.deleteAllFromRealm();
                }
            });
        }
    };


    public static void offlineMode(List<NewsFeedModel> newsFeedModels, MainFeedPresenter presenter){
        for(NewsFeedModel model : newsFeedModels){
            presenter.loadForOfflineMode(model.getLink());
            Log.d("OFFLINE_MODE", model.getLink());
        }
    }

    public static void clearOfflineCache(Context context){ //TODO doesn't work correct
        Realm realm = Realm.getDefaultInstance();
        newsFeedModelList = realm.where(NewsFeedModel.class).findAllAsync();
        newsModelList = realm.where(NewsModel.class).findAllAsync();

        newsFeedModelList.addChangeListener(newsFeedListener);
        newsModelList.addChangeListener(newsModelListener);

        PicassoTools.clearCache(Picasso.with(context));

    }*/

}
