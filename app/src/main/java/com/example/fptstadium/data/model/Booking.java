package com.example.fptstadium.data.model;

import java.util.Date;
import java.util.List;

public class Booking {
    private String id;
    public String fieldId;
    public String fieldName;
    public String paymentId;
    public Date date;
    public String status;
    public long totalPrice;
    public List<TimeSlot> TimeSlots;

    public Booking(String id, String fieldId, String fieldName, String paymentId, Date date, String status, long totalPrice, List<TimeSlot> timeSlots) {
        this.id = id;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.paymentId = paymentId;
        this.date = date;
        this.status = status;
        this.totalPrice = totalPrice;
        TimeSlots = timeSlots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<TimeSlot> getTimeSlots() {
        return TimeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        TimeSlots = timeSlots;
    }
}
