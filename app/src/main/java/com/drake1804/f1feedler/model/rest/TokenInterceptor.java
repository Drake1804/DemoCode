package com.drake1804.f1feedler.model.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Pavel.Shkaran on 5/20/2016.
 */
public class TokenInterceptor implements Interceptor {

    private final RestClient mRestClient;

    public TokenInterceptor(RestClient restClient) {
        mRestClient = restClient;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (mRestClient.hasToken()) {
            request = chain.request().newBuilder()
                    .addHeader("Authorization", mRestClient.getToken())
                    .build();
        }

        Response response = chain.proceed(request);

        if (response.code() == 401) {
            mRestClient.refreshToken();
            request = chain.request().newBuilder()
                    .addHeader("Authorization", mRestClient.getToken())
                    .build();
            return chain.proceed(request);
        } else
            return response;
    }
}
