package com.example.fptstadium.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class PrefsHelper {
    private static final String PREFS_NAME = "fptstadium_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ACCESS_TOKEN_EXPIRES_AT = "access_token_expires_at";
    private static final String KEY_REFRESH_TOKEN_EXPIRES_AT = "refresh_token_expires_at";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences prefs;

    @Inject
    public PrefsHelper(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Legacy token methods for backward compatibility
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    // New token methods
    public void saveAccessToken(String accessToken) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void saveAccessTokenExpiresAt(long expiresAt) {
        prefs.edit().putLong(KEY_ACCESS_TOKEN_EXPIRES_AT, expiresAt).apply();
    }

    public long getAccessTokenExpiresAt() {
        return prefs.getLong(KEY_ACCESS_TOKEN_EXPIRES_AT, 0);
    }

    public void saveRefreshTokenExpiresAt(long expiresAt) {
        prefs.edit().putLong(KEY_REFRESH_TOKEN_EXPIRES_AT, expiresAt).apply();
    }

    public long getRefreshTokenExpiresAt() {
        return prefs.getLong(KEY_REFRESH_TOKEN_EXPIRES_AT, 0);
    }

    public void clearAllTokens() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_ACCESS_TOKEN_EXPIRES_AT)
                .remove(KEY_REFRESH_TOKEN_EXPIRES_AT)
                .apply();
    }

    // User info methods
    public void saveUserId(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void saveUserName(String userName) {
        prefs.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public void saveUserEmail(String userEmail) {
        prefs.edit().putString(KEY_USER_EMAIL, userEmail).apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public void clearUserInfo() {
        prefs.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_EMAIL)
                .apply();
    }

    public void clearAll() {
        clearAllTokens();
        clearUserInfo();
    }
}
