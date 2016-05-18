package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.BuildConfig;
import com.drake1804.f1feedler.model.SessionModel;
import com.drake1804.f1feedler.utils.Tweakables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
public class RestClient {

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
                                        .header("X-FinAnts-Application-Id","YvvP2Ai7rPx3IcrzNZXs40y9DsAXXmZvkENguP1Z")
                                        .header("X-FinAnts-REST-API-Key", "mgajZdld0VHhWsFnjcy04gphlnctjFTkPeLhuGKE")
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Tweakables.BASE_API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restAPI = retrofit.create(RestAPI.class);
    }


    /*********************************************************************************************/


    public Observable<SessionModel> getToken(String username, String password){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        return restAPI.getToken(jsonObject);
    }

}
