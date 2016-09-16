package com.drake1804.f1feedler.model.rest;

import android.text.TextUtils;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.model.CommentsWrapper;
import com.drake1804.f1feedler.model.ItemSessionResponseModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.RefreshTokenResponseModel;
import com.drake1804.f1feedler.model.SignInResponseModel;
import com.drake1804.f1feedler.model.SignUpResponseModel;
import com.drake1804.f1feedler.utils.Tweakables;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public class RestClient implements TokenManager {

    private static RestAPI restAPI;
    private static RestClient restClient;

    public static RestClient getInstance() {
        if (restClient == null) {
            restClient = new RestClient();
        }
        return restClient;
    }

    private RestClient() {
        setupRestClient();
    }

    public void setupRestClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ?
                        HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new TokenInterceptor(RestClient.this))
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Tweakables.BASE_API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restAPI = retrofit.create(RestAPI.class);
    }


    /*********************************************************************************************/


    public Observable<SignInResponseModel> signIn(String username, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        return restAPI.signIn(jsonObject).doOnNext(new Action1<SignInResponseModel>() {
            @Override
            public void call(SignInResponseModel responseModel) {
                if (responseModel.isSuccess())
                    RestClient.this.updateSession(responseModel.getSession());
            }
        });
    }

    public Observable<SignUpResponseModel> signUp(String username, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        return restAPI.signUp(jsonObject);
    }

    private Call<RefreshTokenResponseModel> refreshToken(String refreshToken) {
        return restAPI.refreshToken(refreshToken);
    }

    public Observable<NewsFeedWrapper> getFeed(int page) {
        return restAPI.getFeed(page, Tweakables.MAX_FEED_NEWS, "createdAt,desc");
    }

    public Observable<CommentsWrapper> getCommentsForNews(String newsId) {
        return restAPI.getCommentsForNews(newsId);
    }

    @Override
    public boolean hasToken() {
        return !TextUtils.isEmpty((String) Hawk.get(Tweakables.HAWK_KEY_TOKEN));
    }

    @Override
    public String getToken() {
        return Hawk.get(Tweakables.HAWK_KEY_TOKEN);
    }

    @Override
    public String refreshToken() {
        if (!TextUtils.isEmpty((String) Hawk.get(Tweakables.HAWK_KEY_REFRESH_TOKEN))) {
            Call<RefreshTokenResponseModel> call =
                    refreshToken((String) Hawk.get(Tweakables.HAWK_KEY_REFRESH_TOKEN));
            try {
                final retrofit2.Response<RefreshTokenResponseModel> finalSession = call.execute();

                if (finalSession.isSuccessful() && finalSession.body().isSuccess()) {
                    this.updateSession(finalSession.body().getSession());
                    return finalSession.body().getSession().getToken();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void updateSession(ItemSessionResponseModel session) {
        Hawk.put(Tweakables.HAWK_KEY_TOKEN, session.getToken());
        Hawk.put(Tweakables.HAWK_KEY_REFRESH_TOKEN, session.getRefreshToken());
    }

    @Override
    public void cleanToken() {
        Hawk.remove(Tweakables.HAWK_KEY_TOKEN);
    }

    @Override
    public void cleanSession() {
        Hawk.remove(Tweakables.HAWK_KEY_TOKEN);
        Hawk.remove(Tweakables.HAWK_KEY_REFRESH_TOKEN);
    }

}
