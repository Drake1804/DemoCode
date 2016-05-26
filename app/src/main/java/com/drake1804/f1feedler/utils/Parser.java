package com.drake1804.f1feedler.utils;

import android.support.annotation.Nullable;

import com.drake1804.f1feedler.model.NewsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

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



    public void parseNews(final String url, @Nullable final String imageUrl){
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
                        if(url.contains(Tweakables.F1NEWS)){
                            Elements head = document.getElementsByAttributeValue("class", "post_head");
                            Elements body = document.getElementsByAttributeValue("class", "post_body");

                            savePage(url, head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"), body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                        } else if(url.contains(Tweakables.F1WORLD)){

                        } else if(url.contains(Tweakables.CHAMPIONAT)){
                            Elements top = document.getElementsByAttributeValue("id", "story");
                            Elements article = top.get(0).getElementsByAttributeValue("class", "field-item even");

                            savePage(url, imageUrl, article.html());
                        }
                    }
                });
    }


    private void savePage(final String url, @Nullable final String imageUrl, final String text){
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
        void onError(String message);
    }

}
