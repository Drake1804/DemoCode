package com.drake1804.f1feedler.utils;

import android.text.TextUtils;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.Source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
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
 * Created by Pavel.Shkaran on 5/23/2016.
 */
public class Parser {


    private IOnData iOnData;

    public Parser(IOnData iOnData) {
        this.iOnData = iOnData;
    }

    public void parseFeed(){
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
        .subscribeOn(Schedulers.newThread())
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
                List<NewsFeedModel> newsFeedModels = new ArrayList<>();
                for(Source s : sources){
                    Elements topLevel = s.getDocument().getElementsByTag("item");

                    switch (s.getName()){
                        case Tweakables.F1NEWS:
                            try {
                                for(Element element : topLevel){
                                    NewsFeedModel newsFeedModel = new NewsFeedModel();
                                    newsFeedModel.setResource(Tweakables.F1NEWS);
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
                            try {
                                for(Element element : topLevel){
                                    NewsFeedModel newsFeedModel = new NewsFeedModel();
                                    newsFeedModel.setResource(Tweakables.F1WORLD);
                                    newsFeedModel.setTitle(element.getElementsByTag("title").text());
                                    newsFeedModel.setDescription(element.getElementsByTag("description").text());
                                    newsFeedModel.setLink(element.getElementsByTag("link").text());
                                    if(TextUtils.equals(newsFeedModel.getLink(), "")){
                                        newsFeedModel.setLink(element.getElementsByTag("guid").text());
                                    }
                                    newsFeedModel.setCreatingDate(element.getElementsByTag("pubDate").text());
                                    newsFeedModel.setImageUrl(element.getElementsByTag("image").tagName("url").text());
                                    newsFeedModels.add(newsFeedModel);
                                }
                                saveNewsLinks(newsFeedModels);
                            } catch (Exception ignored){
                                ignored.printStackTrace();
                            }
                            break;
                        case Tweakables.CHAMPIONAT:
                            try {
                                for(Element element : topLevel){
                                    NewsFeedModel newsFeedModel = new NewsFeedModel();
                                    newsFeedModel.setResource(Tweakables.CHAMPIONAT);
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
                    }
                }
                saveNewsLinks(newsFeedModels);
            }
        });

    }

    public void parseNews(final String url){
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
                        iOnData.onError(e.getLocalizedMessage());
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
                            iOnData.onError(e.getMessage());
                        }

                        @Override
                        public void onNext(Document document) {
                            Elements head = document.getElementsByAttributeValue("class", "post_head");
                            Elements body = document.getElementsByAttributeValue("class", "post_body");

                            savePage(url, head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"), body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                        }
                    });
        } else if(url.contains(Tweakables.F1WORLD)){

        } else if(url.contains(Tweakables.CHAMPIONAT)){

        }
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        DataSourceController.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                iOnData.onDataFeed(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }


    private void savePage(final String url, final String imageUrl, final String text){
        DataSourceController.getRealm().beginTransaction();
        NewsModel newsModel = new NewsModel();
        newsModel.setUrl(url);
        newsModel.setImageUrl(imageUrl);
        newsModel.setText(text);
        DataSourceController.getRealm().copyToRealmOrUpdate(newsModel);
        DataSourceController.getRealm().commitTransaction();

        iOnData.onDataDetails(newsModel.getImageUrl(), newsModel.getText());
    }

    public interface IOnData {
        void onDataDetails(String imageUrl, String text);
        void onDataFeed(List<NewsFeedModel> data);
        void onError(String message);
    }

}
