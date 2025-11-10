package com.example.fptstadium.ui.field;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fptstadium.R;
import com.example.fptstadium.api.FieldService;
import com.example.fptstadium.data.model.response.FieldDetailResponse;
import com.example.fptstadium.ui.pricing.FieldPricingActivity;
import com.google.android.material.button.MaterialButton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FieldDetailActivity extends AppCompatActivity {

    private static final String TAG = "FieldDetailActivity";
    public static final String EXTRA_FIELD_ID = "FIELD_ID";

    @Inject
    FieldService fieldService;

    private ImageView fieldMainImage;
    private ImageView fieldBlueprintImage;
    private TextView fieldName;
    private TextView fieldType;
    private TextView fieldPrice;
    private TextView fieldLocation;
    private TextView fieldAddress;
    private TextView fieldDescription;
    private TextView fieldArea;
    private MaterialButton viewMapButton;
    private MaterialButton viewPricingButton;
    private MaterialButton bookFieldButton;
    private ProgressBar progressBar;

    private String fieldId;
    private FieldDetailResponse.FieldDetail currentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.field_detail_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get field ID from intent
        fieldId = getIntent().getStringExtra(EXTRA_FIELD_ID);
        if (fieldId == null || fieldId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Enable ActionBar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sân");
        }

        initViews();
        loadFieldDetail();
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
        fieldMainImage = findViewById(R.id.field_main_image);
        fieldBlueprintImage = findViewById(R.id.field_blueprint_image);
        fieldName = findViewById(R.id.field_name);
        fieldType = findViewById(R.id.field_type);
        fieldPrice = findViewById(R.id.field_price);
        fieldLocation = findViewById(R.id.field_location);
        fieldAddress = findViewById(R.id.field_address);
        fieldDescription = findViewById(R.id.field_description);
        fieldArea = findViewById(R.id.field_area);
        viewMapButton = findViewById(R.id.view_map_button);
        viewPricingButton = findViewById(R.id.view_pricing_button);
        bookFieldButton = findViewById(R.id.book_field_button);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadFieldDetail() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Loading field detail for ID: " + fieldId);

        fieldService.getFieldById(fieldId).enqueue(new Callback<FieldDetailResponse>() {
            @Override
            public void onResponse(Call<FieldDetailResponse> call, Response<FieldDetailResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentField = response.body().getData();
                    Log.d(TAG, "Field detail loaded successfully: " + currentField.getName());
                    displayFieldDetail(currentField);
                } else {
                    Log.e(TAG, "Failed to load field detail. Response code: " + response.code());
                    Toast.makeText(FieldDetailActivity.this, "Không thể tải thông tin sân", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FieldDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading field detail", t);
                Toast.makeText(FieldDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayFieldDetail(FieldDetailResponse.FieldDetail field) {
        // Set action bar title with field name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(field.getName());
        }

        // Load main image
        String mainImageUrl = field.getImageUrl();
        if (mainImageUrl != null && !mainImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(mainImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.primaryColor)
                    .error(R.color.primaryColor)
                    .centerCrop()
                    .into(fieldMainImage);
        } else {
            // No image, set primaryColor background
            fieldMainImage.setBackgroundResource(R.color.primaryColor);
        }

        // Load blueprint image
        String blueprintUrl = field.getBluePrintImageUrl();
        if (blueprintUrl != null && !blueprintUrl.isEmpty()) {
            Glide.with(this)
                    .load(blueprintUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.primaryColor)
                    .error(R.color.primaryColor)
                    .centerCrop()
                    .into(fieldBlueprintImage);

            // Make blueprint clickable to view full screen
            fieldBlueprintImage.setOnClickListener(v -> openImageFullScreen(blueprintUrl));
        } else {
            // No blueprint, set primaryColor background
            fieldBlueprintImage.setBackgroundResource(R.color.primaryColor);
        }

        // Set field information
        fieldName.setText(field.getName());

        // Field type
        if (field.getFieldType() != null && field.getFieldType().getName() != null) {
            fieldType.setText(field.getFieldType().getName());
            fieldType.setVisibility(View.VISIBLE);
        } else {
            fieldType.setVisibility(View.GONE);
        }

        // Price
        if (field.getPricePerHour() != null) {
            String priceText = String.format(java.util.Locale.getDefault(),
                    "%,.0f VND/giờ", field.getPricePerHour());
            fieldPrice.setText(priceText);
        }

        // Location
        String location = field.getDistrict() + ", " + field.getCity();
        fieldLocation.setText(location);

        // Address
        fieldAddress.setText(field.getAddress());

        // Description
        if (field.getDescription() != null && !field.getDescription().isEmpty()) {
            fieldDescription.setText(field.getDescription());
        } else {
            fieldDescription.setText("Chưa có mô tả");
        }

        // Area
        if (field.getArea() != null) {
            String areaText = String.format(java.util.Locale.getDefault(),
                    "%.0f m²", field.getArea());
            fieldArea.setText(areaText);
        } else {
            fieldArea.setText("N/A");
        }

        // Map button
        viewMapButton.setOnClickListener(v -> openMap(field));

        // View Pricing button
        viewPricingButton.setOnClickListener(v -> viewPricing(field));

        // Book button
        bookFieldButton.setOnClickListener(v -> bookField(field));
    }

    private void openImageFullScreen(String imageUrl) {
        // Open image in browser or use a custom image viewer
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivity(intent);
    }

    private void openMap(FieldDetailResponse.FieldDetail field) {
        String mapUrl = field.getMapUrl();

        // If mapUrl is available, use it
        if (mapUrl != null && !mapUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        }
        // Otherwise, use coordinates if available
        else if (field.getLatitude() != null && field.getLongitude() != null) {
            String geoUri = String.format(java.util.Locale.US,
                    "geo:%f,%f?q=%f,%f(%s)",
                    field.getLatitude(),
                    field.getLongitude(),
                    field.getLatitude(),
                    field.getLongitude(),
                    field.getName());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Không có thông tin bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewPricing(FieldDetailResponse.FieldDetail field) {
        Intent intent = new Intent(this, FieldPricingActivity.class);
        intent.putExtra(FieldPricingActivity.EXTRA_FIELD_ID, field.getId());
        intent.putExtra(FieldPricingActivity.EXTRA_FIELD_NAME, field.getName());
        intent.putExtra(FieldPricingActivity.EXTRA_FIELD_ADDRESS, field.getAddress());
        startActivity(intent);
    }

    private void bookField(FieldDetailResponse.FieldDetail field) {
        // Navigate to BookingActivity
        Intent intent = new Intent(this, com.example.fptstadium.ui.booking.BookingActivity.class);
        intent.putExtra(com.example.fptstadium.ui.booking.BookingActivity.EXTRA_FIELD_ID, field.getId());
        intent.putExtra(com.example.fptstadium.ui.booking.BookingActivity.EXTRA_FIELD_NAME, field.getName());
        intent.putExtra(com.example.fptstadium.ui.booking.BookingActivity.EXTRA_FIELD_ADDRESS, field.getAddress());
        intent.putExtra(com.example.fptstadium.ui.booking.BookingActivity.EXTRA_PRICE_PER_HOUR, field.getPricePerHour() != null ? field.getPricePerHour() : 0.0);
        startActivity(intent);
    }
}