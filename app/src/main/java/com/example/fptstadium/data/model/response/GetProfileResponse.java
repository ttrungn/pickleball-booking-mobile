package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class GetProfileResponse {
    @SerializedName("data")
    private UserProfileData data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public UserProfileData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setData(UserProfileData data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
