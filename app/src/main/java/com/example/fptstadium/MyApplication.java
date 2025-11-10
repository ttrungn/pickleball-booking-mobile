package com.example.fptstadium;

import android.app.Application;

import com.example.fptstadium.utils.NotificationHelper;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Tạo notification channel khi app khởi động
        NotificationHelper.createNotificationChannel(this);
    }
}