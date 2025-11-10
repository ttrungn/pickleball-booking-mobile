package com.example.fptstadium.data.model.response;

import com.example.fptstadium.data.model.TimeSlot;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBookingResponse {
    @SerializedName("pageNumber")
    private int pageNumber;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("hasPreviousPage")
    private boolean hasPreviousPage;

    @SerializedName("hasNextPage")
    private boolean hasNextPage;

    @SerializedName("data")
    private List<BookingData> data; // Changed from BookingData to List<BookingData>

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public List<BookingData> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class BookingData {
        @SerializedName("id")
        private String id;
        @SerializedName("fieldId")
        private String fieldId;
        @SerializedName("fieldName")
        private String fieldName;
        @SerializedName("paymentId")
        private String paymentId;
        @SerializedName("date")
        private String date;
        @SerializedName("status")
        private String status;
        @SerializedName("totalPrice")
        private long totalPrice;
        @SerializedName("timeSlots")
        private List<TimeSlot> timeSlots;

        public String getId() {
            return id;
        }

        public String getFieldId() {
            return fieldId;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public long getTotalPrice() {
            return totalPrice;
        }

        public List<TimeSlot> getTimeSlots() {
            return timeSlots;
        }
    }
}
