package com.example.fptstadium.ui.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.response.MomoPaymentResponse;
import com.example.fptstadium.ui.booking.BookingViewModel;
import com.example.fptstadium.ui.profile.PaymentViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentExampleActivity extends AppCompatActivity {

    private static final String TAG = "PaymentExample";

    private PaymentViewModel viewModel;
    private BookingViewModel bookingViewModel;
    private ProgressBar progressBar;
    private TextView tvAmount;
    private TextView tvFieldName;
    private TextView tvBookingDate;
    private LinearLayout llTimeSlots;
    private Button btnPayWithMomo;
    private Button btnCancelPayment;

    private String bookingId;
    private long amount;
    private String fieldName;
    private String bookingDate;
    private ArrayList<String> timeSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_example);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payment_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enable ActionBar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get data from intent
        bookingId = getIntent().getStringExtra("BOOKING_ID");
        amount = getIntent().getLongExtra("AMOUNT", 0);
        fieldName = getIntent().getStringExtra("FIELD_NAME");
        bookingDate = getIntent().getStringExtra("BOOKING_DATE");
        timeSlots = getIntent().getStringArrayListExtra("TIME_SLOTS");

        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
        displayBookingInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvAmount = findViewById(R.id.tvAmount);
        tvFieldName = findViewById(R.id.tvFieldName);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        llTimeSlots = findViewById(R.id.llTimeSlots);
        btnPayWithMomo = findViewById(R.id.btnPayWithMomo);
        btnCancelPayment = findViewById(R.id.btnCancelPayment);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
    }

    private void setupListeners() {
        btnPayWithMomo.setOnClickListener(v -> {
            Log.d(TAG, "User clicked Pay with MoMo for booking: " + bookingId);
            initiatePayment();
        });

        btnCancelPayment.setOnClickListener(v -> {
            Log.d(TAG, "User clicked Cancel Payment for booking: " + bookingId);
            showCancelConfirmationDialog();
        });
    }

    /**
     * Hiển thị dialog xác nhận hủy thanh toán
     */
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy thanh toán")
                .setMessage("Bạn có chắc chắn muốn hủy thanh toán này? Đơn đặt sân sẽ bị hủy.")
                .setPositiveButton("Hủy thanh toán", (dialog, which) -> {
                    cancelPayment();
                })
                .setNegativeButton("Quay lại", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Gọi API để hủy booking
     */
    private void cancelPayment() {
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPayWithMomo.setEnabled(false);
        btnCancelPayment.setEnabled(false);

        Log.d(TAG, "Canceling booking: " + bookingId);

        bookingViewModel.cancelBooking(bookingId).observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            btnPayWithMomo.setEnabled(true);
            btnCancelPayment.setEnabled(true);

            // Kiểm tra response null (do 204 No Content) hoặc isSuccess
            if (response == null || response.isSuccess()) {
                Toast.makeText(this, "Đã hủy thanh toán thành công", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Booking cancelled successfully: " + bookingId);

                // Quay về màn hình trước
                finish();
            } else {
                String errorMsg = response.getMessage() != null ? response.getMessage() : "Không thể hủy thanh toán";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to cancel booking: " + errorMsg);
            }
        });
    }

    private void displayBookingInfo() {
        tvFieldName.setText(fieldName != null ? fieldName : "");
        tvAmount.setText(String.format("%,d VND", amount));

        // Display booking date
        if (bookingDate != null && !bookingDate.isEmpty()) {
            tvBookingDate.setText("Ngày đặt: " + formatDate(bookingDate));
        } else {
            tvBookingDate.setText("Ngày đặt: --/--/----");
        }

        // Display time slots - sorted
        llTimeSlots.removeAllViews();
        if (timeSlots != null && timeSlots.size() > 0) {
            // Sort time slots before displaying
            ArrayList<String> sortedTimeSlots = sortTimeSlots(timeSlots);
            for (String timeSlot : sortedTimeSlots) {
                addTimeSlotSimple(timeSlot);
            }
        }
    }

    /**
     * Sort time slots by start time
     */
    private ArrayList<String> sortTimeSlots(ArrayList<String> timeSlots) {
        ArrayList<String> sorted = new ArrayList<>(timeSlots);
        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String slot1, String slot2) {
                // Extract start time from format "07:00 - 08:00"
                try {
                    String start1 = slot1.split(" - ")[0].trim();
                    String start2 = slot2.split(" - ")[0].trim();
                    return start1.compareTo(start2);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return sorted;
    }

    /**
     * Format date from "2025-11-05" to "05/11/2025"
     */
    private String formatDate(String date) {
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
            // Return original if parsing fails
        }
        return date;
    }

    /**
     * Add a simple time slot (no price)
     */
    private void addTimeSlotSimple(String timeRange) {
        TextView tvTime = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        tvTime.setLayoutParams(params);
        tvTime.setText("• " + timeRange);
        tvTime.setTextSize(14);
        tvTime.setTextColor(0xFF424242);
        tvTime.setPadding(16, 0, 0, 0);

        llTimeSlots.addView(tvTime);
    }

    private void initiatePayment() {
        progressBar.setVisibility(View.VISIBLE);
        btnPayWithMomo.setEnabled(false);

        Log.d(TAG, "Requesting MoMo payment URL for bookingId: " + bookingId);

        viewModel.getMomoPaymentUrl(bookingId).observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            btnPayWithMomo.setEnabled(true);

            if (response != null && response.isSuccess() && response.getData() != null) {
                MomoPaymentResponse.MomoPaymentData data = response.getData();
                String payUrl = data.getPayUrl();
                String deeplink = data.getDeeplink();

                Log.d(TAG, "Payment URL received: " + payUrl);
                Log.d(TAG, "Deeplink: " + deeplink);

                // Ưu tiên dùng deeplink để mở MoMo app
                if (deeplink != null && !deeplink.isEmpty()) {
                    openMomoApp(deeplink, payUrl);
                } else if (payUrl != null && !payUrl.isEmpty()) {
                    openMomoWebView(payUrl);
                } else {
                    Toast.makeText(this, "Không nhận được link thanh toán", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Both payUrl and deeplink are empty");
                }
            } else {
                String errorMsg = response != null ? response.getMessage() : "Không thể tạo thanh toán";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Payment URL request failed: " + errorMsg);
            }
        });
    }

    /**
     * Mở MoMo app bằng deeplink
     */
    private void openMomoApp(String deeplink, String fallbackUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.d(TAG, "Opened MoMo app with deeplink");
        } catch (Exception e) {
            Log.w(TAG, "Failed to open MoMo app, falling back to web view: " + e.getMessage());
            // Nếu không mở được MoMo app, fallback sang web view
            if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                openMomoWebView(fallbackUrl);
            } else {
                Toast.makeText(this, "Không thể mở ứng dụng MoMo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Mở MoMo payment trong trình duyệt
     */
    private void openMomoWebView(String payUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl));
            startActivity(intent);
            Log.d(TAG, "Opened payment URL in browser");
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở link thanh toán: " + e.getMessage(),
                         Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to open payment URL", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khi user quay lại từ MoMo app, có thể kiểm tra trạng thái payment
        Log.d(TAG, "onResume - User returned to payment screen");
    }
}
