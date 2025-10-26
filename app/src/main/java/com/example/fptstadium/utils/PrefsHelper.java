package com.example.fptstadium.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class PrefsHelper {
    private static final String PREFS_NAME = "fptstadium_prefs";
    private static final String KEY_TOKEN = "auth_token";

    private final SharedPreferences prefs;

    @Inject
    public PrefsHelper(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
