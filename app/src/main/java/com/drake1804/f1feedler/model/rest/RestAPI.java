package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.model.CommentsWrapper;
import com.drake1804.f1feedler.model.NewsFeedWrapper;
import com.drake1804.f1feedler.model.RefreshTokenResponseModel;
import com.drake1804.f1feedler.model.SignInResponseModel;
import com.drake1804.f1feedler.model.SignUpResponseModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
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
    Observable<SignInResponseModel> signIn(@Body JsonObject json);

    @POST("signup")
    Observable<SignUpResponseModel> signUp(@Body JsonObject json);

    @POST("refresh_token")
    Call<RefreshTokenResponseModel> refreshToken(@Query("refresh_token") String refreshToken);

    @GET("news")
    Observable<NewsFeedWrapper> getFeed(@Query("page") int page, @Query("size") int size, @Query("sort") String sort);

    @GET("news_comments")
    Observable<CommentsWrapper> getCommentsForNews(@Query("id") String newsId);

}
