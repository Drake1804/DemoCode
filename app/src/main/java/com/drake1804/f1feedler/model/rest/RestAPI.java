package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.model.CommentsWrapper;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.SessionModel;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public interface RestAPI {

    @POST("signin")
    Observable<SessionModel> signIn(@Body JsonObject json);

    @POST("signup")
    Observable<SessionModel> signUp(@Body JsonObject json);

    @GET("news")
    Observable<NewsFeedWrapper> getFeed(@Query("country[]") String country, @Query("language[]") String language, @Query("category[]") String category);

    @GET("news_comments")
    Observable<CommentsWrapper> getCommentsForNews(@Query("id") String newsId);

}
