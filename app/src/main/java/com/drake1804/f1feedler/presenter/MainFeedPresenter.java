package com.drake1804.f1feedler.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.MainFeedView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class MainFeedPresenter extends Presenter {

    private MainFeedView view;
    private Context context;
    private Target target = new Target() {
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

    public void getNewsFeed(){
        Realm realm = Realm.getDefaultInstance();
        view.setData(realm.where(NewsFeedModel.class).findAll());
        loadNewsFeed();
    }

    public MainFeedPresenter(MainFeedView view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void loadNewsFeed(){
        view.showDialog();

        new AsyncTask<Void, Document, Document>() {

            @Override
            protected Document doInBackground(Void... params) {
                Document document = null;
                try {
                    document = Jsoup.connect(Tweakables.BASE_FEED_URL).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return document;
            }

            @Override
            protected void onPostExecute(Document document) {
                super.onPostExecute(document);
                List<NewsFeedModel> newsFeedModels = new ArrayList<>();
                try {
                    Elements topLevel = document.getElementsByTag("item");

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
                view.dismissDialog();
            }
        }.execute();
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                view.setData(realm.where(NewsFeedModel.class).findAll());
                realm.close();
            }
        });
    }

    public void parsePage(final String url) {

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

                    savePage(url, head.get(0).getElementsByAttributeValue("itemprop", "contentUrl").attr("src"),
                            body.get(0).getElementsByAttributeValue("itemprop", "articleBody").html());
                } catch (Exception e){}
                view.dismissDialog();
            }
        }.execute();

    }

    private void savePage(final String url, final String imageUrl, final String text){
        final Realm realm = Realm.getDefaultInstance();
        Picasso.with(context)
                .load(imageUrl)
                .into(target);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                NewsModel newsModel = new NewsModel();
                newsModel.setUrl(url);
                newsModel.setImageUrl(imageUrl);
                newsModel.setText(text);
                realm.copyToRealmOrUpdate(newsModel);
            }
        });

    }


    public void loadForOfflineMode(String url){
        Realm realm = Realm.getDefaultInstance();
        NewsModel newsModel = realm.where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parsePage(url);
        }
    }
}
