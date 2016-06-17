package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

public class SignUpResponseModel extends BaseResponseModel {

    @SerializedName("session")
    private ItemSessionResponseModel mSession;

    public ItemSessionResponseModel getSession() {
        return mSession;
    }

    @Override
    public boolean isSuccess() {
        return getError() == null && getSession() != null;
    }

}
