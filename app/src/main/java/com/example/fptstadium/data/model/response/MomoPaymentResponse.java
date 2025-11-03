package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class MomoPaymentResponse {

    @SerializedName("data")
    private MomoPaymentData data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public MomoPaymentData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class MomoPaymentData {
        @SerializedName("partnerCode")
        private String partnerCode;
        @SerializedName("requestId")
        private String requestId;
        @SerializedName("orderId")
        private String orderId;
        @SerializedName("amount")
        private long amount;
        @SerializedName("responseTime")
        private long responseTime;
        @SerializedName("message")
        private String message;
        @SerializedName("resultCode")
        private int resultCode;
        @SerializedName("payUrl")
        private String payUrl;
        @SerializedName("deeplink")
        private String deeplink;
        @SerializedName("qrCodeUrl")
        private String qrCodeUrl;

        public String getPartnerCode() { return partnerCode; }
        public String getRequestId() { return requestId; }
        public String getOrderId() { return orderId; }
        public long getAmount() { return amount; }
        public long getResponseTime() { return responseTime; }
        public String getMessage() { return message; }
        public int getResultCode() { return resultCode; }
        public String getPayUrl() { return payUrl; }
        public String getDeeplink() { return deeplink; }
        public String getQrCodeUrl() { return qrCodeUrl; }
    }
}
