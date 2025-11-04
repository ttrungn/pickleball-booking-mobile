package com.example.fptstadium.data.model;

import com.google.gson.annotations.SerializedName;

public class TimeSlot {
    @SerializedName("id")
    private String id;

    @SerializedName("startTime")
    private String startTime;  // Format từ backend: "HH:mm:ss" (TimeOnly)

    @SerializedName("endTime")
    private String endTime;    // Format từ backend: "HH:mm:ss" (TimeOnly)

    @SerializedName("price")
    private Double price;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    // Getters
    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Helper method để format thời gian
    public String getFormattedTimeRange() {
        if (startTime != null && endTime != null) {
            // Remove seconds if present (HH:mm:ss -> HH:mm)
            String start = formatTime(startTime);
            String end = formatTime(endTime);
            return start + " - " + end;
        }
        return "N/A";
    }

    private String formatTime(String time) {
        if (time == null) return "";
        // If time has seconds, remove them (HH:mm:ss -> HH:mm)
        if (time.length() > 5 && time.charAt(5) == ':') {
            return time.substring(0, 5);
        }
        return time;
    }
}
