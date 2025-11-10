package com.example.fptstadium.data.model.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingRequest {
    @SerializedName("fieldId")
    private String fieldId;

    @SerializedName("date")
    private String date; // Format: "2025-11-04"

    @SerializedName("timeSlotIds")
    private List<String> timeSlotIds; // Chỉ gửi array of IDs

    @SerializedName("totalPrice")
    private long totalPrice; // Tổng số tiền

    public BookingRequest(String fieldId, String date, List<String> timeSlotIds) {
        this.fieldId = fieldId;
        this.date = date;
        this.timeSlotIds = timeSlotIds;
        this.totalPrice = 0;
    }

    public BookingRequest(String fieldId, String date, List<String> timeSlotIds, long totalPrice) {
        this.fieldId = fieldId;
        this.date = date;
        this.timeSlotIds = timeSlotIds;
        this.totalPrice = totalPrice;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTimeSlotIds() {
        return timeSlotIds;
    }

    public void setTimeSlotIds(List<String> timeSlotIds) {
        this.timeSlotIds = timeSlotIds;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
