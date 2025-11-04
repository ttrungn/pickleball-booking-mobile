package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    @SerializedName("data")
    private String data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public String getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
