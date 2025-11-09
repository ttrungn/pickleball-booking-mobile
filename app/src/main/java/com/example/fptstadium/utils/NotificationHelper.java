package com.example.fptstadium.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.fptstadium.MainActivity;
import com.example.fptstadium.R;

/**
 * Helper class để quản lý notifications trong app
 */
public class NotificationHelper {

    // Channel ID cho các notification thanh toán
    public static final String CHANNEL_ID = "payments_channel";
    private static final String CHANNEL_NAME = "Thanh toán";
    private static final String CHANNEL_DESC = "Thông báo về thanh toán và đặt sân";

    /**
     * Tạo Notification Channel (cần thiết cho Android 8.0+)
     * Gọi method này khi app khởi động
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.setShowBadge(true);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Hiển thị notification khi thanh toán thành công
     *
     * @param context Context của activity/fragment
     * @param title Tiêu đề notification
     * @param message Nội dung notification
     * @param bookingId ID của booking (optional, mặc định 0)
     */
    public static void showPaymentSuccessNotification(
            Context context,
            String title,
            String message,
            int bookingId
    ) {
        // Tạo intent để mở app khi click vào notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("open_my_bookings", true); // Mở tab My Bookings

        // Tạo PendingIntent
        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                bookingId,
                intent,
                flags
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Icon nhỏ
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // Tự động đóng khi click
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(context.getResources().getColor(R.color.primaryColor, null));

        // Hiển thị notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Sử dụng timestamp làm notification ID để mỗi notification có ID riêng
            int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            notificationManager.notify(notificationId, builder.build());
        }
    }

    /**
     * Overload method với bookingId mặc định = 0
     */
    public static void showPaymentSuccessNotification(
            Context context,
            String title,
            String message
    ) {
        showPaymentSuccessNotification(context, title, message, 0);
    }

    /**
     * Hiển thị notification thông thường
     */
    public static void showNotification(
            Context context,
            String title,
            String message,
            int notificationId
    ) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                flags
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    /**
     * Hủy notification theo ID
     */
    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
        }
    }

    /**
     * Hủy tất cả notifications
     */
    public static void cancelAllNotifications(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}

