package com.drake1804.f1feedler.model.rest;

import com.drake1804.f1feedler.model.ItemSessionResponseModel;

public interface TokenManager {

    boolean hasToken();
    String getToken();
    String refreshToken();
    void updateSession(ItemSessionResponseModel session);
    void cleanToken();
    void cleanSession();

}
