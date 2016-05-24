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

    public void getNewsFeed(){
        view.setData(DataSourceController.getRealm().where(NewsFeedModel.class).findAll());
        parser.parseFeed();
    }

    public MainFeedPresenter(MainFeedView view, Context context) {
        this.view = view;
        this.context = context;
        parser = new Parser(this);
    }

    @Override
    public void onDataDetails(String imageUrl, String text) {

    }

    @Override
    public void onDataFeed(List<NewsFeedModel> data) {
        view.setData(data);
        view.dismissDialog();
    }

    @Override
    public void onError(String message) {
        view.showMessage(message);
    }
}
