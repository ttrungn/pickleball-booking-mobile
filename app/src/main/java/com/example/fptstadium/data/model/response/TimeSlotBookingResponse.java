package com.example.fptstadium.data.model.response;

import com.example.fptstadium.data.model.TimeSlot;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeSlotBookingResponse {
    @SerializedName("data")
    private List<TimeSlot> data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public List<TimeSlot> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Setters
    public void setData(List<TimeSlot> data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
