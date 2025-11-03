package com.example.fptstadium.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Pricing {
    @SerializedName("id")
    private String id;

    @SerializedName("fieldId")
    private String fieldId;

    @SerializedName("timeSlotId")
    private String timeSlotId;

    @SerializedName("dayOfWeek")
    private int dayOfWeek;  // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    @SerializedName("price")
    private double price;

    // Getters
    public String getId() {
        return id;
    }

    public String getFieldId() {
        return fieldId;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public double getPrice() {
        return price;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Helper method to get day name
    public String getDayOfWeekName() {
        switch (dayOfWeek) {
            case 0: return "Chủ nhật";
            case 1: return "Thứ hai";
            case 2: return "Thứ ba";
            case 3: return "Thứ tư";
            case 4: return "Thứ năm";
            case 5: return "Thứ sáu";
            case 6: return "Thứ bảy";
            default: return "N/A";
        }
    }

    // Helper method to format price
    public String getFormattedPrice() {
        return String.format("%,.0f VNĐ", price);
    }
}

