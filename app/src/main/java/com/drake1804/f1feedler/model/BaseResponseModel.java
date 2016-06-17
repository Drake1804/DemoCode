package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

abstract class BaseResponseModel {

    @SerializedName("error")
    private ItemErrorResponseModel mError;

    public ItemErrorResponseModel getError() {
        return mError;
    }

    public abstract boolean isSuccess();

}
