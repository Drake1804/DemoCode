package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.CommentsWrapper;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.model.rest.RestClient;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Parser;
import com.drake1804.f1feedler.view.view.DetailsView;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class DetailsPresenter extends Presenter implements Parser.IOnData {

    private DetailsView view;
    private Parser parser;


    public DetailsPresenter(DetailsView view) {
        this.view = view;
        parser = new Parser(this);
    }

    public void getPage(String url, String imageUrl){
        NewsModel model = DataSourceController.getRealm().where(NewsModel.class)
                .equalTo("url", url)
                .findFirst();
        if(model != null){
            view.setData(model.getImageUrl(), model.getText());
        } else {
            parser.parseNews(url, imageUrl);
        }
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
                        view.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(CommentsWrapper commentsWrapper) {
                        view.setComments(commentsWrapper.comments);
                    }
                });
    }



    @Override
    public void onDataDetails(String imageUrl, String text) {
        view.setData(imageUrl, text);
        view.dismissDialog();
    }

    @Override
    public void onError(String message) {
        view.showMessage(message);
    }
}
