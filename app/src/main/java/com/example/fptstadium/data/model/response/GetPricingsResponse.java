package com.example.fptstadium.data.model.response;

import com.example.fptstadium.data.model.Pricing;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetPricingsResponse {

    @SerializedName("data")
    private List<Pricing> data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public List<Pricing> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setData(List<Pricing> data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

