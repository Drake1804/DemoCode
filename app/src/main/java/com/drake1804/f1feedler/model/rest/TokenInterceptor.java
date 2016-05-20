package com.drake1804.f1feedler.model.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Pavel.Shkaran on 5/20/2016.
 */
public class TokenInterceptor implements Interceptor {

    private final TokenManager mTokenManager;

    public TokenInterceptor(TokenManager mTokenManager) {
        this.mTokenManager = mTokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request initialRequest = chain.request();
        Request modifiedRequest = initialRequest;
        if (mTokenManager.hasToken()) {
            modifiedRequest = initialRequest.newBuilder()
                    .addHeader("USER_TOKEN", mTokenManager.getToken())
                    .build();
        }
        Response response = chain.proceed(modifiedRequest);
        boolean unauthorized = response.code() == 401;
        if (unauthorized) {
            mTokenManager.clearToken();
            String newToken = mTokenManager.refreshToken();
            modifiedRequest = initialRequest.newBuilder()
                    .addHeader("USER_TOKEN", mTokenManager.getToken())
                    .build();
            return chain.proceed(modifiedRequest);
        }
        return response;
    }
}
