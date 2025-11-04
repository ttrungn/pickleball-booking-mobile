package com.example.fptstadium.ui.payment;

import android.os.Bundle;
import android.net.Uri;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.request.MomoPaymentCallbackRequest;

import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentResultActivity extends AppCompatActivity {

    private static final String TAG = "PaymentResultActivity";

    private ImageView ivStatus;
    private TextView tvTitle, tvSubtitle, tvAmount;
    private Button btnDone;
    private PaymentResultViewModel viewModel;
    private String lastPostedOrderId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        viewModel = new ViewModelProvider(this).get(PaymentResultViewModel.class);
        Uri data = getIntent().getData();
        logDeeplink("onCreate", data);
        populateFromDeeplink(data);
        setupButtons();
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Uri data = intent.getData();
        logDeeplink("onNewIntent", data);
        populateFromDeeplink(data);
    }

    private void bindViews() {
        ivStatus = findViewById(R.id.ivStatus);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvAmount = findViewById(R.id.tvAmount);
        btnDone = findViewById(R.id.btnDone);
    }

    private void setupButtons() {
        btnDone.setOnClickListener(v -> {
            // Redirect to home (MainActivity) and clear the back stack
            Intent intent = new Intent(this, com.example.fptstadium.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void populateFromDeeplink(Uri data) {
        // Default UI state
        boolean isSuccess = false;
        String resultCode = null;
        String message = null;
        String amount = null;
        String partnerCode = null;
        String orderId = null;
        String requestId = null;
        String orderInfo = null;
        String orderType = null;
        String transId = null;
        String payType = null;
        String responseTime = null;
        String extraData = null;
        String signature = null;

        if (data != null) {
            // Common MoMo parameters (names can vary depending on integration)
            resultCode = getParam(data, "resultCode", getParam(data, "ResultCode", null));
            message = getParam(data, "message", getParam(data, "Message", null));
            amount = getParam(data, "amount", getParam(data, "Amount", null));
            partnerCode = getParam(data, "partnerCode", null);
            orderId = getParam(data, "orderId", null);
            requestId = getParam(data, "requestId", null);
            orderInfo = getParam(data, "orderInfo", null);
            orderType = getParam(data, "orderType", null);
            transId = getParam(data, "transId", null);
            payType = getParam(data, "payType", null);
            responseTime = getParam(data, "responseTime", null);
            extraData = getParam(data, "extraData", null);
            signature = getParam(data, "signature", null);

            // Consider success if resultCode == 0
            try {
                isSuccess = resultCode != null && Integer.parseInt(resultCode) == 0;
            } catch (NumberFormatException ignored) { }
        }

        // Update UI
        ivStatus.setImageResource(isSuccess ? R.drawable.ic_success : R.drawable.ic_error);
        tvTitle.setText(isSuccess ? getString(R.string.payment_success_title) : getString(R.string.payment_failed_title));
        tvSubtitle.setText(message != null ? message : (isSuccess ? getString(R.string.payment_success_title) : getString(R.string.payment_failed_title)));
        tvAmount.setText(amount != null ? formatCurrency(amount) : "-");
        // Highlight amount color by status
        tvAmount.setTextColor(isSuccess ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));

        // Submit payment to backend only when succeeded and avoid duplicate posts by orderId
        if (isSuccess && orderId != null && !orderId.equals(lastPostedOrderId)) {
            lastPostedOrderId = orderId;
            try {
                MomoPaymentCallbackRequest req = new MomoPaymentCallbackRequest(
                        partnerCode,
                        orderId,
                        requestId,
                        parseLong(amount),
                        orderInfo,
                        orderType,
                        parseLong(transId),
                        parseInt(resultCode),
                        message,
                        payType,
                        parseLong(responseTime),
                        extraData,
                        signature
                );
                viewModel.submitMomoPayment(req).observe(this, success -> {
                    Log.d(TAG, "submitMomoPayment => " + success);
                });
            } catch (Exception e) {
                Log.w(TAG, "Failed to build/submit momo callback: " + e.getMessage());
            }
        }
    }

    private String getParam(Uri data, String key, String fallback) {
        String v = data.getQueryParameter(key);
        return v != null ? v : fallback;
    }

    private String formatCurrency(String raw) {
        try {
            long value = Long.parseLong(raw);
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
            return nf.format(value) + " VND";
        } catch (Exception e) {
            return raw;
        }
    }

    private long parseLong(String s) {
        try { return s == null ? 0L : Long.parseLong(s); } catch (Exception e) { return 0L; }
    }

    private int parseInt(String s) {
        try { return s == null ? -1 : Integer.parseInt(s); } catch (Exception e) { return -1; }
    }

    private void logDeeplink(String source, Uri data) {
        if (data == null) {
            Log.w(TAG, source + ": data is null");
            return;
        }
        Log.d(TAG, source + ": uri=" + data.toString());
        Log.d(TAG, "scheme=" + data.getScheme() + ", host=" + data.getHost() + ", path=" + data.getPath());
        try {
            java.util.Set<String> names = data.getQueryParameterNames();
            if (names == null || names.isEmpty()) {
                Log.d(TAG, "no query parameters");
            } else {
                for (String name : names) {
                    Log.d(TAG, "param[" + name + "]=" + data.getQueryParameter(name));
                }
            }
        } catch (Throwable t) {
            Log.w(TAG, "Failed to enumerate query params: " + t.getMessage());
        }
    }
}