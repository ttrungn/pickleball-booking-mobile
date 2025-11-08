package com.example.fptstadium.data.model;

import com.google.gson.annotations.SerializedName;

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

    // New fields from backend response to avoid extra time slot API calls
    @SerializedName("timeSlotStartTime")
    private String timeSlotStartTime; // e.g. "HH:mm:ss"

    @SerializedName("timeSlotEndTime")
    private String timeSlotEndTime;   // e.g. "HH:mm:ss"

    @SerializedName("rangeStartTime")
    private String rangeStartTime;    // e.g. "HH:mm:ss"

    @SerializedName("rangeEndTime")
    private String rangeEndTime;      // e.g. "HH:mm:ss"

    // Getters
    public String getId() { return id; }
    public String getFieldId() { return fieldId; }
    public String getTimeSlotId() { return timeSlotId; }
    public int getDayOfWeek() { return dayOfWeek; }
    public double getPrice() { return price; }

    public String getTimeSlotStartTime() { return timeSlotStartTime; }
    public String getTimeSlotEndTime() { return timeSlotEndTime; }
    public String getRangeStartTime() { return rangeStartTime; }
    public String getRangeEndTime() { return rangeEndTime; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFieldId(String fieldId) { this.fieldId = fieldId; }
    public void setTimeSlotId(String timeSlotId) { this.timeSlotId = timeSlotId; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setPrice(double price) { this.price = price; }

    public void setTimeSlotStartTime(String timeSlotStartTime) { this.timeSlotStartTime = timeSlotStartTime; }
    public void setTimeSlotEndTime(String timeSlotEndTime) { this.timeSlotEndTime = timeSlotEndTime; }
    public void setRangeStartTime(String rangeStartTime) { this.rangeStartTime = rangeStartTime; }
    public void setRangeEndTime(String rangeEndTime) { this.rangeEndTime = rangeEndTime; }

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

    // Helper: format time string HH:mm:ss -> HH:mm
    private String toHm(String time) {
        if (time == null) return null;
        if (time.length() > 5 && time.charAt(5) == ':') return time.substring(0, 5);
        return time;
    }

    // Returns a formatted time range if present in this response; otherwise null
    public String getFormattedTimeRangeOrNull() {
        String start;
        String end;
        // Prefer range if provided
        if (rangeStartTime != null && rangeEndTime != null) {
            start = toHm(rangeStartTime);
            end = toHm(rangeEndTime);
        } else if (timeSlotStartTime != null && timeSlotEndTime != null) {
            start = toHm(timeSlotStartTime);
            end = toHm(timeSlotEndTime);
        } else {
            return null;
        }
        if (start != null && end != null) {
            return start + " - " + end;
        }
        return null;
    }
}
