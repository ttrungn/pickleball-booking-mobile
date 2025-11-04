package com.example.fptstadium.data.model;

public enum BookingStatus {
    PENDING(0, "Chờ xử lý"),
    CONFIRMED(1, "Đã xác nhận"),
    CANCELLED(2, "Đã hủy"),
    COMPLETED(3, "Hoàn thành");

    private final int value;
    private final String displayName;

    BookingStatus(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromValue(int value) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return PENDING;
    }
}

