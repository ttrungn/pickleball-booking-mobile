package com.example.fptstadium.ui.pricing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.api.PricingService;
import com.example.fptstadium.api.TimeSlotService;
import com.example.fptstadium.data.model.Pricing;
import com.example.fptstadium.data.model.TimeSlot;
import com.example.fptstadium.data.model.response.GetPricingsResponse;
import com.example.fptstadium.data.model.response.GetTimeSlotResponse;
import com.example.fptstadium.ui.adapter.PricingAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FieldPricingActivity extends AppCompatActivity {

    private static final String TAG = "FieldPricingActivity";
    public static final String EXTRA_FIELD_ID = "field_id";
    public static final String EXTRA_FIELD_NAME = "field_name";
    public static final String EXTRA_FIELD_ADDRESS = "field_address";

    @Inject
    PricingService pricingService;

    @Inject
    TimeSlotService timeSlotService;

    private MaterialToolbar toolbar;
    private TextView tvFieldName;
    private TextView tvFieldAddress;
    private RecyclerView recyclerViewPricing;
    private ProgressBar loadingIndicator;
    private TextView tvEmptyState;
    private PricingAdapter adapter;
    
    // Map to store time slots by ID
    private Map<String, TimeSlot> timeSlotMap = new HashMap<>();

    private String fieldId;
    private String fieldName;
    private String fieldAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_pricing);

        // Get data from intent
        fieldId = getIntent().getStringExtra(EXTRA_FIELD_ID);
        fieldName = getIntent().getStringExtra(EXTRA_FIELD_NAME);
        fieldAddress = getIntent().getStringExtra(EXTRA_FIELD_ADDRESS);

        if (fieldId == null || fieldId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        displayFieldInfo();
        loadPricings();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvFieldName = findViewById(R.id.tv_field_name);
        tvFieldAddress = findViewById(R.id.tv_field_address);
        recyclerViewPricing = findViewById(R.id.recycler_view_pricing);
        loadingIndicator = findViewById(R.id.loading_indicator);
        tvEmptyState = findViewById(R.id.tv_empty_state);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new PricingAdapter(new ArrayList<>(), timeSlotMap);
        recyclerViewPricing.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPricing.setAdapter(adapter);
    }

    private void displayFieldInfo() {
        if (fieldName != null) {
            tvFieldName.setText(fieldName);
        }
        if (fieldAddress != null) {
            tvFieldAddress.setText(fieldAddress);
        }
    }

    private void loadPricings() {
        Log.d(TAG, "Loading pricings for field: " + fieldId);
        showLoading(true);

        pricingService.getPricingsByField(fieldId).enqueue(new Callback<GetPricingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetPricingsResponse> call, @NonNull Response<GetPricingsResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    GetPricingsResponse pricingResponse = response.body();

                    if (pricingResponse.isSuccess() && pricingResponse.getData() != null) {
                        List<Pricing> pricings = pricingResponse.getData();
                        Log.d(TAG, "Received " + pricings.size() + " pricings");

                        if (pricings.isEmpty()) {
                            showEmptyState(true);
                        } else {
                            // Load time slots for all pricings
                            loadTimeSlotsForPricings(pricings);
                        }
                    } else {
                        showEmptyState(true);
                        Toast.makeText(FieldPricingActivity.this,
                                "Không có bảng giá: " + pricingResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showEmptyState(true);
                    Log.e(TAG, "Response not successful: " + response.code());
                    Toast.makeText(FieldPricingActivity.this,
                            "Lỗi khi tải bảng giá: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetPricingsResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showEmptyState(true);
                Log.e(TAG, "Failed to load pricings", t);
                Toast.makeText(FieldPricingActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadTimeSlotsForPricings(List<Pricing> pricings) {
        showLoading(true);

        // Get unique time slot IDs
        List<String> timeSlotIds = new ArrayList<>();
        for (Pricing pricing : pricings) {
            String timeSlotId = pricing.getTimeSlotId();
            if (timeSlotId != null && !timeSlotIds.contains(timeSlotId)) {
                timeSlotIds.add(timeSlotId);
            }
        }

        Log.d(TAG, "Loading " + timeSlotIds.size() + " unique time slots");

        // Counter to track completed requests
        final int[] completedRequests = {0};
        final int totalRequests = timeSlotIds.size();

        if (totalRequests == 0) {
            showEmptyState(false);
            adapter.updatePricings(pricings);
            showLoading(false);
            return;
        }

        // Load each time slot
        for (String timeSlotId : timeSlotIds) {
            timeSlotService.getTimeSlotById(timeSlotId).enqueue(new Callback<GetTimeSlotResponse>() {
                @Override
                public void onResponse(@NonNull Call<GetTimeSlotResponse> call, @NonNull Response<GetTimeSlotResponse> response) {
                    completedRequests[0]++;

                    if (response.isSuccessful() && response.body() != null) {
                        GetTimeSlotResponse timeSlotResponse = response.body();
                        if (timeSlotResponse.isSuccess() && timeSlotResponse.getData() != null) {
                            TimeSlot timeSlot = timeSlotResponse.getData();
                            timeSlotMap.put(timeSlot.getId(), timeSlot);
                            Log.d(TAG, "Loaded time slot: " + timeSlot.getId() + " -> " + timeSlot.getFormattedTimeRange());
                        }
                    } else {
                        Log.e(TAG, "Failed to load time slot " + timeSlotId + ": " + response.code());
                    }

                    // When all requests complete, update the adapter
                    if (completedRequests[0] >= totalRequests) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showEmptyState(false);
                            adapter.updatePricings(pricings);
                            Log.d(TAG, "All time slots loaded. Total in map: " + timeSlotMap.size());
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetTimeSlotResponse> call, @NonNull Throwable t) {
                    completedRequests[0]++;
                    Log.e(TAG, "Failed to load time slot " + timeSlotId, t);

                    // When all requests complete (even with failures), update the adapter
                    if (completedRequests[0] >= totalRequests) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showEmptyState(false);
                            adapter.updatePricings(pricings);
                        });
                    }
                }
            });
        }
    }

    private void showLoading(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerViewPricing.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean isEmpty) {
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewPricing.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
