package com.drake1804.f1feedler.presenter;

import android.os.AsyncTask;

import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.MainFeedView;

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
public class MainFeedPresenter {

    private MainFeedView view;

    public void getNewsFeed(){
        Realm realm = Realm.getDefaultInstance();
        view.setData(realm.where(NewsFeedModel.class).findAll());
        loadNewsFeed();
    }

    public MainFeedPresenter(MainFeedView view) {
        this.view = view;
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
                        newsFeedModel.setImageUrl(element.getElementsByTag("enclosure").get(0).attributes().get("url"));

                        newsFeedModels.add(newsFeedModel);
                    }
                    saveNewsLinks(newsFeedModels);
                } catch (Exception e){}
                view.dismissDialog();
            }
        }.execute();
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        NewsFeedModel newsFeedModel = realm.createObject(NewsFeedModel.class);
                        newsFeedModel.setLink(list.get(i).getLink());
                        newsFeedModel.setTitle(list.get(i).getTitle());
                        newsFeedModel.setDescription(list.get(i).getDescription());
                        newsFeedModel.setImageUrl(list.get(i).getImageUrl());
                        newsFeedModel.setCreatingDate(list.get(i).getCreatingDate());
                    } catch (Exception e){}
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                view.setData(realm.where(NewsFeedModel.class).findAll());
            }
        });
    }
}
