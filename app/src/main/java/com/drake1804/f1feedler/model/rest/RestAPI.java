package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.model.SessionModel;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Pavel.Shkaran on 5/18/2016.
 */
public interface RestAPI {

    @POST("signin")
    Observable<SessionModel> signIn(@Body JsonObject json);

    @POST("signup")
    Observable<SessionModel> signUp(@Body JsonObject json);

}
