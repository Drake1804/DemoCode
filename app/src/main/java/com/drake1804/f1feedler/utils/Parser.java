package com.drake1804.f1feedler.utils;

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

    public static final String F1NEWS = "f1news.ru";
    public static final String F1WORLD = "f1-world.ru";
    public static final String CHAMPIONAT = "championat.com";

    private IOnData iOnData;

    public Parser(IOnData iOnData) {
        this.iOnData = iOnData;
    }

    public void parseFeed(){
        Observable.create(new Observable.OnSubscribe<List<Source>>() {
            @Override
            public void call(Subscriber<? super List<Source>> subscriber) {
                List<Source> sources = new ArrayList<>();
                sources.add(new Source(F1NEWS, Tweakables.F1NEWS_FEED_URL));
                sources.add(new Source(CHAMPIONAT, Tweakables.CHAMPIONAT_FEED_URL));
                sources.add(new Source(F1WORLD, Tweakables.F1WORLD_FEED_URL));

                for(Source source : sources){
                    Document document;
                    try {
                        document = Jsoup.connect(source.getUrl()).get();
                        Timber.d("Loaded: "+source.getUrl());
                        source.setDocument(document);
                    } catch (IOException e) {
                        iOnData.onError(e.getLocalizedMessage());
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
                for(Source s : sources){
                    switch (s.getName()){
                        case F1NEWS:
                            List<NewsFeedModel> newsFeedModels = new ArrayList<>();
                            try {
                                Elements topLevel = s.getDocument().getElementsByTag("item");

                                for(Element element : topLevel){
                                    NewsFeedModel newsFeedModel = new NewsFeedModel();
                                    newsFeedModel.setTitle(element.getElementsByTag("title").text());
                                    newsFeedModel.setDescription(element.getElementsByTag("description").text());
                                    newsFeedModel.setLink(element.getElementsByTag("link").text());
                                    newsFeedModel.setCreatingDate(element.getElementsByTag("pubDate").text());
                                    if(element.getElementsByTag("enclosure").size() > 0){
                                        newsFeedModel.setImageUrl(element.getElementsByTag("enclosure").get(0).attributes().get("url"));
                                    }
                                    newsFeedModels.add(newsFeedModel);
                                }
                                saveNewsLinks(newsFeedModels);
                            } catch (Exception ignored){}
                            break;
                        case F1WORLD:
                            break;
                        case CHAMPIONAT:
                            break;
                    }
                }
            }
        });

    }

    public void parseNews(final String url){
        if(url.contains(F1NEWS)){
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
                    .subscribeOn(Schedulers.newThread())
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
        } else if(url.contains(F1WORLD)){

        } else if(url.contains(CHAMPIONAT)){

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
