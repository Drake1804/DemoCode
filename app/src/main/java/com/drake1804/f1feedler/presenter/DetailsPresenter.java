package com.drake1804.f1feedler.presenter;

import android.os.AsyncTask;

import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.view.view.DetailsView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class DetailsPresenter extends Presenter {

    private DetailsView view;
    private String url;


    public DetailsPresenter(DetailsView view) {
        this.view = view;
    }

    public void getPage(String url){
        this.url = url;
        Realm realm = Realm.getDefaultInstance();
        NewsModel newsModel = realm.where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parsePage(url);
        } else {
            view.setData(newsModel.getImageUrl(), newsModel.getText());
        }

    }

    public void parsePage(final String url) {
        view.showDialog();

        new AsyncTask<Void, Void, Document>(){

            @Override
            protected Document doInBackground(Void... params) {
                Document document = null;
                try {
                    document = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }

            @Override
            protected void onPostExecute(Document document) {
                super.onPostExecute(document);
                try {
                    Elements head = document.getElementsByAttributeValue("class", "post_head");
                    Elements body = document.getElementsByAttributeValue("class", "post_body");

                    savePage(head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"), body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                } catch (Exception e){}
                view.dismissDialog();
            }
        }.execute();

    }

    private void savePage(final String imageUrl, final String text){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                NewsModel newsModel = new NewsModel();
                newsModel.setUrl(url);
                newsModel.setImageUrl(imageUrl);
                newsModel.setText(text);
                realm.copyToRealmOrUpdate(newsModel);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                NewsModel newsModel = realm.where(NewsModel.class)
                        .equalTo("imageUrl", imageUrl)
                        .findFirst();
                view.setData(newsModel.getImageUrl(), newsModel.getText());
            }
        });

    }

    private void loadForOfflineMode(String url){
        Realm realm = Realm.getDefaultInstance();
        NewsModel newsModel = realm.where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parsePage(url);
        }
    }

}
