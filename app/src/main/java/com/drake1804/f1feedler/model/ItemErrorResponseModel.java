package com.drake1804.f1feedler.model;

import com.google.gson.annotations.SerializedName;

class ItemErrorResponseModel {

    @SerializedName("code")
    private Integer mCode;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("description")
    private String mDescription;

    public Integer getCode() {
        return mCode;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getDescription() {
        return mDescription;
    }

}
