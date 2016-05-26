package com.drake1804.f1feedler.presenter;

import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Parser;
import com.drake1804.f1feedler.view.view.DetailsView;

/**
 * Created by Pavel.Shkaran on 5/13/2016.
 */
public class DetailsPresenter extends Presenter implements Parser.IOnData {

    private DetailsView view;
    private String url;
    private Parser parser;


    public DetailsPresenter(DetailsView view) {
        this.view = view;
        parser = new Parser(this);
    }

    public void getPage(String url, String imageUrl){
        this.url = url;
        NewsModel model = DataSourceController.getRealm().where(NewsModel.class)
                .equalTo("url", url)
                .findFirst();
        if(model != null){
            view.setData(model.getImageUrl(), model.getText());
        } else {
            parser.parseNews(url, imageUrl);
        }
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
