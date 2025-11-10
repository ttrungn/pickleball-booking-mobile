package com.example.fptstadium.ui.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.api.TimeSlotService;
import com.example.fptstadium.data.model.TimeSlot;
import com.example.fptstadium.data.model.request.BookingRequest;
import com.example.fptstadium.data.model.response.TimeSlotBookingResponse;
import com.example.fptstadium.ui.adapter.TimeSlotAdapter;
import com.example.fptstadium.ui.auth.LoginActivity;
import com.example.fptstadium.ui.payment.PaymentExampleActivity;
import com.example.fptstadium.utils.PrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class BookingActivity extends AppCompatActivity {

    private static final String TAG = "BookingActivity";
    public static final String EXTRA_FIELD_ID = "FIELD_ID";
    public static final String EXTRA_FIELD_NAME = "FIELD_NAME";
    public static final String EXTRA_FIELD_ADDRESS = "FIELD_ADDRESS";
    public static final String EXTRA_PRICE_PER_HOUR = "PRICE_PER_HOUR";

    @Inject
    TimeSlotService timeSlotService;

    @Inject
    PrefsHelper prefsHelper;

    private BookingViewModel viewModel;
    private TimeSlotAdapter timeSlotAdapter;

    // Views
    private TextView tvFieldName;
    private TextView tvFieldAddress;
    private TextView tvSelectedDate;
    private MaterialButton btnSelectDate;
    private RecyclerView rvTimeSlots;
    private ProgressBar progressBar;
    private TextView tvNoTimeSlots;
    private MaterialCardView summaryCard;
    private TextView tvSelectedSlotsCount;
    private TextView tvTotalPrice;
    private MaterialButton btnConfirmBooking;
    private LinearLayout llBookingWarning;

    // Data
    private String fieldId;
    private String fieldName;
    private String fieldAddress;
    private double pricePerHour = 0;
    private Date selectedDate;
    private List<TimeSlot> availableTimeSlots;
    private Set<String> bookedSlotIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra xem user đã login chưa
        if (!checkLoginStatus()) {
            return;
        }

        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.booking), (v, insets) -> {
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
        fieldId = getIntent().getStringExtra(EXTRA_FIELD_ID);
        fieldName = getIntent().getStringExtra(EXTRA_FIELD_NAME);
        fieldAddress = getIntent().getStringExtra(EXTRA_FIELD_ADDRESS);
        pricePerHour = getIntent().getDoubleExtra(EXTRA_PRICE_PER_HOUR, 0);

        if (fieldId == null || fieldId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupListeners();

        // Set field info
        tvFieldName.setText(fieldName != null ? fieldName : "Sân");
        tvFieldAddress.setText(fieldAddress != null ? fieldAddress : "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Kiểm tra xem user đã login chưa
     * @return true nếu đã login, false nếu chưa
     */
    private boolean checkLoginStatus() {
        String token = prefsHelper.getAccessToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt sân", Toast.LENGTH_LONG).show();
            // Chuyển đến màn hình login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    private void initViews() {
        tvFieldName = findViewById(R.id.tvFieldName);
        tvFieldAddress = findViewById(R.id.tvFieldAddress);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        progressBar = findViewById(R.id.progressBar);
        tvNoTimeSlots = findViewById(R.id.tvNoTimeSlots);
        summaryCard = findViewById(R.id.summaryCard);
        tvSelectedSlotsCount = findViewById(R.id.tvSelectedSlotsCount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        llBookingWarning = findViewById(R.id.llBookingWarning);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);
    }

    private void setupRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter((timeSlot, isSelected) -> {
            updateSummary();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rvTimeSlots.setLayoutManager(layoutManager);
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }

    private void setupListeners() {

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());

        // Tự động set ngày hôm nay và load time slots
        setTodayAsDefault();
    }

    /**
     * Set ngày hôm nay làm ngày mặc định và load time slots
     */
    private void setTodayAsDefault() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        selectedDate = calendar.getTime();
        updateSelectedDateUI();
        loadTimeSlots();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 0, 0, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    selectedDate = selected.getTime();
                    updateSelectedDateUI();
                    loadTimeSlots();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateSelectedDateUI() {
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvSelectedDate.setText(sdf.format(selectedDate));
        }
    }

    private void loadTimeSlots() {
        if (selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNoTimeSlots.setVisibility(View.GONE);
        rvTimeSlots.setVisibility(View.GONE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(selectedDate);

        Log.d(TAG, "Loading time slots for field: " + fieldId + ", date: " + dateString);

        timeSlotService.getTimeSlots(fieldId, dateString).enqueue(new Callback<TimeSlotBookingResponse>() {
            @Override
            public void onResponse(Call<TimeSlotBookingResponse> call, Response<TimeSlotBookingResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TimeSlot> allTimeSlots = response.body().getData();

                    if (allTimeSlots != null && !allTimeSlots.isEmpty()) {
                        // Separate available and booked slots
                        availableTimeSlots = new ArrayList<>();
                        bookedSlotIds.clear();

                        // Kiểm tra xem có phải hôm nay không
                        Calendar today = Calendar.getInstance();
                        Calendar selected = Calendar.getInstance();
                        selected.setTime(selectedDate);

                        boolean isToday = today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) &&
                                         today.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR);

                        int currentHour = today.get(Calendar.HOUR_OF_DAY);

                        Log.d(TAG, "Is today: " + isToday + ", Current hour: " + currentHour);

                        for (TimeSlot slot : allTimeSlots) {
                            // Nếu slot đã được đặt, thêm vào danh sách đã đặt
                            if (!slot.isAvailable()) {
                                bookedSlotIds.add(slot.getId());
                            }
                            // Nếu là hôm nay, kiểm tra xem giờ bắt đầu có quá giờ hiện tại không
                            else if (isToday && slot.getStartTime() != null) {
                                // Parse startTime (format: "HH:mm:ss")
                                try {
                                    String[] timeParts = slot.getStartTime().split(":");
                                    int slotHour = Integer.parseInt(timeParts[0]);

                                    // Nếu giờ bắt đầu <= giờ hiện tại, mark as booked (không cho chọn)
                                    if (slotHour <= currentHour) {
                                        bookedSlotIds.add(slot.getId());
                                        Log.d(TAG, "Slot " + slot.getFormattedTimeRange() + " is in the past, disabled");
                                    } else {
                                        availableTimeSlots.add(slot);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing time for slot: " + slot.getId(), e);
                                    availableTimeSlots.add(slot);
                                }
                            }
                            // Nếu không phải hôm nay và slot available, thêm vào danh sách available
                            else {
                                availableTimeSlots.add(slot);
                            }
                        }

                        // Set all slots to adapter (both available and booked)
                        timeSlotAdapter.setTimeSlots(allTimeSlots);
                        timeSlotAdapter.setBookedSlots(bookedSlotIds);
                        timeSlotAdapter.clearSelection();

                        rvTimeSlots.setVisibility(View.VISIBLE);
                        tvNoTimeSlots.setVisibility(View.GONE);

                        Log.d(TAG, "Loaded " + allTimeSlots.size() + " time slots (" +
                                availableTimeSlots.size() + " available, " +
                                bookedSlotIds.size() + " booked/past)");
                    } else {
                        tvNoTimeSlots.setText("Không có khung giờ nào khả dụng");
                        tvNoTimeSlots.setVisibility(View.VISIBLE);
                        rvTimeSlots.setVisibility(View.GONE);
                    }

                    updateSummary();
                } else {
                    Log.e(TAG, "Failed to load time slots. Response code: " + response.code());
                    tvNoTimeSlots.setText("Không thể tải khung giờ");
                    tvNoTimeSlots.setVisibility(View.VISIBLE);
                    Toast.makeText(BookingActivity.this, "Không thể tải khung giờ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TimeSlotBookingResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvNoTimeSlots.setText("Lỗi kết nối: " + t.getMessage());
                tvNoTimeSlots.setVisibility(View.VISIBLE);
                Log.e(TAG, "Error loading time slots", t);
                Toast.makeText(BookingActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummary() {
        List<TimeSlot> selectedSlots = timeSlotAdapter.getSelectedTimeSlots();
        int count = selectedSlots.size();

        if (count > 0) {
            summaryCard.setVisibility(View.VISIBLE);
            llBookingWarning.setVisibility(View.VISIBLE);
            btnConfirmBooking.setEnabled(true);

            tvSelectedSlotsCount.setText(String.valueOf(count));

            // Tính tổng tiền dựa trên giá thực tế của từng timeslot
            double totalPrice = 0;
            for (TimeSlot slot : selectedSlots) {
                if (slot.getPrice() != null) {
                    totalPrice += slot.getPrice();
                } else {
                    // Fallback nếu timeslot không có giá
                    totalPrice += pricePerHour;
                }
            }

            String priceText = String.format(Locale.getDefault(), "%,.0f VND", totalPrice);
            tvTotalPrice.setText(priceText);

            Log.d(TAG, "Selected " + count + " slots, Total: " + totalPrice + " VND");
        } else {
            summaryCard.setVisibility(View.GONE);
            llBookingWarning.setVisibility(View.GONE);
            btnConfirmBooking.setEnabled(false);
        }
    }

    private void confirmBooking() {
        List<TimeSlot> selectedSlots = timeSlotAdapter.getSelectedTimeSlots();

        if (selectedSlots.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một khung giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra lại login trước khi tạo booking
        if (!checkLoginStatus()) {
            return;
        }

        // Tính tổng tiền
        double totalPrice = 0;
        for (TimeSlot slot : selectedSlots) {
            if (slot.getPrice() != null) {
                totalPrice += slot.getPrice();
            } else {
                totalPrice += pricePerHour;
            }
        }

        // Lưu totalPrice vào final variable để dùng trong lambda
        final long finalTotalPrice = (long) totalPrice;

        // Chuyển đổi date sang format yyyy-MM-dd
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = apiDateFormat.format(selectedDate);

        // Lấy danh sách timeSlotIds từ selectedSlots
        List<String> timeSlotIds = new ArrayList<>();
        for (TimeSlot slot : selectedSlots) {
            timeSlotIds.add(slot.getId());
        }

        // Create booking request
        BookingRequest request = new BookingRequest(fieldId, dateString, timeSlotIds, finalTotalPrice);

        progressBar.setVisibility(View.VISIBLE);
        btnConfirmBooking.setEnabled(false);

        Log.d(TAG, "Creating booking for field: " + fieldId + ", date: " + dateString +
              ", timeSlotIds: " + timeSlotIds + ", totalPrice: " + finalTotalPrice + " VND");

        viewModel.createBooking(request).observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            btnConfirmBooking.setEnabled(true);

            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_SHORT).show();

                // Navigate to payment
                String bookingId = response.getData();
                if (bookingId != null && !bookingId.isEmpty()) {
                    Log.d(TAG, "Booking created successfully with ID: " + bookingId + ", Total: " + finalTotalPrice + " VND");

                    // Prepare time slots array for payment screen
                    ArrayList<String> timeSlotsList = new ArrayList<>();
                    for (TimeSlot slot : selectedSlots) {
                        timeSlotsList.add(slot.getFormattedTimeRange());
                    }

                    // Format date for payment screen
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String bookingDateStr = selectedDate != null ? dateFormat.format(selectedDate) : "";

                    // Chuyển đến trang thanh toán
                    Intent intent = new Intent(this, PaymentExampleActivity.class);
                    intent.putExtra("BOOKING_ID", bookingId);
                    intent.putExtra("AMOUNT", finalTotalPrice);
                    intent.putExtra("FIELD_NAME", fieldName);
                    intent.putExtra("BOOKING_DATE", bookingDateStr);
                    intent.putStringArrayListExtra("TIME_SLOTS", timeSlotsList);
                    startActivity(intent);
                    finish();
                } else {
                    // Nếu không có bookingId, quay về màn hình trước
                    finish();
                }
            } else {
                // Fix lỗi Toast crash khi message null
                String errorMsg = (response != null && response.getMessage() != null)
                    ? response.getMessage()
                    : "Đặt sân thất bại. Vui lòng thử lại.";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Booking failed: " + errorMsg);
            }
        });
    }
}
