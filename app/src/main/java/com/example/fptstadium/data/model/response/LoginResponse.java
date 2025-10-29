package com.example.fptstadium.data.model.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("data")
    private LoginData data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public LoginData getData() {
        return data;
    }

    public static class LoginData {
        @SerializedName("accessToken")
        private String accessToken;

        @SerializedName("refreshToken")
        private String refreshToken;

        @SerializedName("accessTokenExpiresAt")
        private long accessTokenExpiresAt;

        @SerializedName("refreshTokenExpiresAt")
        private long refreshTokenExpiresAt;

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public long getAccessTokenExpiresAt() {
            return accessTokenExpiresAt;
        }

        public long getRefreshTokenExpiresAt() {
            return refreshTokenExpiresAt;
        }
    }
}


