package com.drake1804.f1feedler.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.adapter.MainFeedAdapter;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.ResourceModel;
import com.drake1804.f1feedler.model.SocialModel;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.presenter.MainFeedPresenter;
import com.drake1804.f1feedler.view.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
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

    private Context context;
    private Realm realm;
    private ProgressDialog dialog;
    private int preloaded;
    private int newsForSave;
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

    public OfflineMode(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    public void createMode(ProgressDialog dialog){
        this.dialog = dialog;
        dialog.show();
        RestClient.getInstance().getFeed(0)
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
                        saveNewsLinks(newsFeedWrapper.embedded.items);
                    }
                });
    }

    private void saveNewsLinks(final List<NewsFeedModel> list){
        realm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(list), () -> {
            for (int i = 0; i < list.size(); i++) {
                newsForSave = list.size();
                NewsFeedModel model = list.get(i);
                loadForOfflineNews(model.getLink(), model.getImageUrl());
            }
            Timber.d("news for save: "+newsForSave);
            dialog.setMax(newsForSave);

        }, Throwable::printStackTrace);
    }

    public void parseNews(final String url, @Nullable final String imageUrl){
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {
                Document document;
                try {
                    document = Jsoup.connect(url).get();
                    Timber.d("Loaded: %s",url);
                    subscriber.onNext(document);
                } catch (IOException e) {
                    newsForSave--;
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

    private void savePage(final String url, final String imageUrl, final String text){
        if(!imageUrl.isEmpty()){
            Picasso.with(context)
                    .load(imageUrl)
                    .into(target);
        } else {
            newsForSave--;
        }

        preloaded++;
        new Handler().post(() -> dialog.setProgress(preloaded));
        if(preloaded == newsForSave){
            dialog.dismiss();
        }
        Timber.d("preloaded loaded: %s", preloaded);

        realm.beginTransaction();
        NewsModel newsModel = new NewsModel();
        newsModel.setUrl(url);
        newsModel.setImageUrl(imageUrl);
        newsModel.setText(text);
        realm.copyToRealmOrUpdate(newsModel);
        realm.commitTransaction();
    }

    private void loadForOfflineNews(String url, String imageUrl){
        NewsModel newsModel = realm.where(NewsModel.class).equalTo("url", url).findFirst();
        if(newsModel == null){
            parseNews(url, imageUrl);
        }
    }


    public void clearOfflineData(MainFeedAdapter adapter, MainFeedPresenter presenter){
        RealmResults<NewsFeedModel> newsFeedModels = realm.where(NewsFeedModel.class).findAll();
        RealmResults<NewsModel> newsModels = realm.where(NewsModel.class).findAll();
        RealmResults<ResourceModel> resourceModels = realm.where(ResourceModel.class).findAll();
        RealmResults<SocialModel> socialModels = realm.where(SocialModel.class).findAll();

        realm.executeTransaction(realm1 -> {
            newsFeedModels.deleteAllFromRealm();
            newsModels.deleteAllFromRealm();
            resourceModels.deleteAllFromRealm();
            socialModels.deleteAllFromRealm();
            adapter.getNewsFeedModels().clear();
            adapter.notifyDataSetChanged();
            Timber.d("Offline data was cleared!");
            Toast.makeText(context, context.getString(R.string.data_was_cleared), Toast.LENGTH_LONG);
            presenter.getNewsFeed(0);
        });
    }

}
