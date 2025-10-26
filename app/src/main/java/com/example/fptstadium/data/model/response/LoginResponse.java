package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private int code;
    private String status;
    private String message;
    private String traceId;
    @SerializedName("data")
    private LoginData data;


    public int getCode() { return code; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getTraceId() { return traceId; }
    public LoginData getData() {
        return data;
    }

    public static class LoginData {
        @SerializedName("isPremium")

        private boolean isPremium;

        @SerializedName("token")
        private String token;

        public String getToken () {
            return token;
        }
    }
}


