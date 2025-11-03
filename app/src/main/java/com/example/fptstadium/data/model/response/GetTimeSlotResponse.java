package com.example.fptstadium.data.model.response;

import com.example.fptstadium.data.model.TimeSlot;
import com.google.gson.annotations.SerializedName;

public class GetTimeSlotResponse {
    @SerializedName("data")
    private TimeSlot data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public TimeSlot getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setData(TimeSlot data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

