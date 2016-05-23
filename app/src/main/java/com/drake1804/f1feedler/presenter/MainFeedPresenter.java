package com.drake1804.f1feedler.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Parser;
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
public class MainFeedPresenter extends Presenter implements Parser.IOnData {

    private MainFeedView view;
    private Context context;
    private Parser parser;
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
        view.setData(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
        parser.parseFeed();
    }

    public MainFeedPresenter(MainFeedView view, Context context) {
        this.view = view;
        this.context = context;
        parser = new Parser(this);
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

    @Override
    public void onDataDetails(String imageUrl, String text) {

    }

    @Override
    public void onDataFeed(List<NewsFeedModel> data) {
        view.setData(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
        view.dismissDialog();
    }

    @Override
    public void onError(String message) {
        view.showMessage(message);
    }
}
