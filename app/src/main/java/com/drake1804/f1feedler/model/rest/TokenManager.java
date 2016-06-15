package com.drake1804.f1feedler.model.rest;

/**
 * Created by Pavel.Shkaran on 5/20/2016.
 */
public interface TokenManager {
    String getToken();
    boolean hasToken();
    void clearToken();
    void refreshToken();
}
