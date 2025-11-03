package com.example.fptstadium.ui.field;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fptstadium.R;
import com.example.fptstadium.api.FieldService;
import com.example.fptstadium.data.model.response.FieldDetailResponse;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
    private MaterialButton bookFieldButton;
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbar;

    private String fieldId;
    private FieldDetailResponse.FieldDetail currentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);

        // Get field ID from intent
        fieldId = getIntent().getStringExtra(EXTRA_FIELD_ID);
        if (fieldId == null || fieldId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadFieldDetail();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
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
        bookFieldButton = findViewById(R.id.book_field_button);
        progressBar = findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // Set CollapsingToolbar color to primaryColor instead of purple
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.primaryColor));
        collapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.primaryColor));
    }

    private void setupToolbar() {
        // Toolbar setup is now done in initViews
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
        // Set toolbar title
        collapsingToolbar.setTitle(field.getName());

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

    private void bookField(FieldDetailResponse.FieldDetail field) {
        // TODO: Implement booking functionality
        Toast.makeText(this, "Đang phát triển tính năng đặt sân cho " + field.getName(),
            Toast.LENGTH_SHORT).show();
    }
}

