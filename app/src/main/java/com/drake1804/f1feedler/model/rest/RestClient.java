package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.SessionModel;
import com.drake1804.f1feedler.utils.DataSourceController;
import com.drake1804.f1feedler.utils.Tweakables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import io.realm.RealmResults;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

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

    static {
        setupRestClient();
    }

    public static void setupRestClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request original = chain.request();

                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Content-Type", "application/json")
                                        .header("X-FinAnts-Application-Id", Tweakables.APP_ID)
                                        .header("X-FinAnts-REST-API-Key", Tweakables.REST_KEY)
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                /*.addInterceptor(new TokenInterceptor(restClient))*/
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


    public Observable<SessionModel> signIn(String username, String password){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        return restAPI.signIn(jsonObject);
    }

    public Observable<SessionModel> signUp(String username, String password){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        return restAPI.signUp(jsonObject);
    }

    public Observable<NewsFeedWrapper> getFeed(){
        return restAPI.getFeed("ru", 10);
    }

    @Override
    public String getToken() {

        return null;
    }

    @Override
    public boolean hasToken() {
        return false;
    }

    @Override
    public void clearToken() {
        RealmResults<SessionModel> realmResults = DataSourceController.getRealm().where(SessionModel.class).findAll();
        DataSourceController.getRealm().beginTransaction();
        realmResults.deleteAllFromRealm();
        DataSourceController.getRealm().commitTransaction();
    }

    @Override
    public String refreshToken() {
        return null;
    }
}
