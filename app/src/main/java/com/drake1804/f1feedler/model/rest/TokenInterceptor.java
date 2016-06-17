package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.utils.Tweakables;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final TokenManager mTokenManager;

    public TokenInterceptor(TokenManager tokenManager) {
        mTokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-FinAnts-Application-Id", Tweakables.APP_ID)
                .header("X-FinAnts-REST-API-Key", Tweakables.REST_KEY)
                .method(original.method(), original.body());

        if (mTokenManager.hasToken()) {
            requestBuilder.header("Authorization", mTokenManager.getToken());
        }

        original = requestBuilder.build();

        Response response = chain.proceed(original);
        boolean unauthorized = response.code() == 401;
        if (unauthorized) {
            mTokenManager.cleanToken();
            String newToken = mTokenManager.refreshToken();
            original = original.newBuilder()
                    .header("Authorization", newToken)
                    .build();
            return chain.proceed(original);
        }
        return response;
    }
}
