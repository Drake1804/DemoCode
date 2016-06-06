package com.drake1804.f1feedler.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.model.CommentsWrapper;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.TimeAgo;
import com.drake1804.f1feedler.utils.Tweakables;
import com.drake1804.f1feedler.view.view.DetailsView;
import com.squareup.picasso.Picasso;

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
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class DetailsPresenter extends Presenter {

    private DetailsView view;
    private Context context;

    public DetailsPresenter(DetailsView view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void getPage(String url, String imageUrl){
        NewsModel model = view.getRealm().where(NewsModel.class)
                .equalTo("url", url)
                .findFirst();
        if(model != null){
            view.setData(model.getImageUrl(), model.getText());
        } else {
            parseNews(url, imageUrl);
        }
    }

    public void parseNews(final String url, @Nullable final String imageUrl){
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {
                Document document;
                try {
                    document = Jsoup.connect(url).get();
                    Timber.d("Loaded: %s", url);
                    subscriber.onNext(document);
                } catch (IOException e) {
                    if(BuildConfig.DEBUG) {
                        view.showMessage(e.getLocalizedMessage());
                        Timber.e(e.getLocalizedMessage());
                    }
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
                        if(BuildConfig.DEBUG){
                            view.showMessage(e.getMessage());
                            Timber.e(e.getLocalizedMessage());
                        }
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

    private void savePage(final String url, @Nullable final String imageUrl, final String text){
        view.getRealm().beginTransaction();
        NewsModel newsModel = new NewsModel();
        newsModel.setUrl(url);
        newsModel.setImageUrl(imageUrl);
        newsModel.setText(text);
        view.getRealm().copyToRealmOrUpdate(newsModel);
        view.getRealm().commitTransaction();

        view.setData(newsModel.getImageUrl(), newsModel.getText());
        view.dismissDialog();
    }

    public void getComments(String newsId){
        RestClient.getInstance().getCommentsForNews(newsId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentsWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(BuildConfig.DEBUG){
                            view.showMessage(e.getMessage());
                            Timber.e(e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onNext(CommentsWrapper commentsWrapper) {
                        view.setComments(commentsWrapper.comments);
                    }
                });
    }

    public void setHeader(String logoUrl, String resource, long date){
        Picasso.with(context)
                .load(logoUrl)
                .into(view.getLogoView());
        view.getResource().setText(resource);
        view.getDate().setText(TimeAgo.toDuration(System.currentTimeMillis() - date));
    }
}
