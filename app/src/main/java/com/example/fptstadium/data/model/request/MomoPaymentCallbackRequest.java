package com.example.fptstadium.data.model.request;

import com.google.gson.annotations.SerializedName;

public class MomoPaymentCallbackRequest {

    @SerializedName("partnerCode")
    private String partnerCode;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("requestId")
    private String requestId;

    @SerializedName("amount")
    private long amount;

    @SerializedName("orderInfo")
    private String orderInfo;

    @SerializedName("orderType")
    private String orderType;

    @SerializedName("transId")
    private long transId;

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("message")
    private String message;

    @SerializedName("payType")
    private String payType;

    @SerializedName("responseTime")
    private long responseTime;

    @SerializedName("extraData")
    private String extraData;

    @SerializedName("signature")
    private String signature;

    public MomoPaymentCallbackRequest(String partnerCode, String orderId, String requestId, long amount,
                                      String orderInfo, String orderType, long transId, int resultCode,
                                      String message, String payType, long responseTime, String extraData,
                                      String signature) {
        this.partnerCode = partnerCode;
        this.orderId = orderId;
        this.requestId = requestId;
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.orderType = orderType;
        this.transId = transId;
        this.resultCode = resultCode;
        this.message = message;
        this.payType = payType;
        this.responseTime = responseTime;
        this.extraData = extraData;
        this.signature = signature;
    }

    // Getters (optional if needed elsewhere)
    public String getPartnerCode() { return partnerCode; }
    public String getOrderId() { return orderId; }
    public String getRequestId() { return requestId; }
    public long getAmount() { return amount; }
    public String getOrderInfo() { return orderInfo; }
    public String getOrderType() { return orderType; }
    public long getTransId() { return transId; }
    public int getResultCode() { return resultCode; }
    public String getMessage() { return message; }
    public String getPayType() { return payType; }
    public long getResponseTime() { return responseTime; }
    public String getExtraData() { return extraData; }
    public String getSignature() { return signature; }
}
