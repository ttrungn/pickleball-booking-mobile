package com.example.fptstadium.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.BookingStatus;
import com.example.fptstadium.data.model.response.GetBookingResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyBookingsActivity extends AppCompatActivity implements MyBookingsAdapter.OnBookingActionListener {

    private RecyclerView rvBookings;
    private ProgressBar progressBar;
    private LinearLayout llEmptyState;
    private LinearLayout llErrorState;
    private LinearLayout llPagination;
    private TextView tvErrorMessage;
    private TextView tvPageInfo;
    private TextView tvItemsInfo;
    private Button btnRetry;
    private Button btnPreviousPage;
    private Button btnNextPage;
    private ImageView btnBack;

    // Filter chips
    private ChipGroup chipGroupStatus;
    private Chip chipAll;
    private Chip chipPending;
    private Chip chipCancelled;
    private Chip chipCompleted;

    private MyBookingsViewModel viewModel;
    private MyBookingsAdapter adapter;

    // Pagination variables
    private List<GetBookingResponse.BookingData> allBookings = new ArrayList<>();
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 5;

    // Current filter
    private Integer currentFilterStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        setupFilterChips();

        // Load bookings
        viewModel.loadUserBookings();
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        progressBar = findViewById(R.id.progressBar);
        llEmptyState = findViewById(R.id.llEmptyState);
        llErrorState = findViewById(R.id.llErrorState);
        llPagination = findViewById(R.id.llPagination);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        tvItemsInfo = findViewById(R.id.tvItemsInfo);
        btnRetry = findViewById(R.id.btnRetry);
        btnPreviousPage = findViewById(R.id.btnPreviousPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        btnBack = findViewById(R.id.btnBack);

        // Filter chips
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipCancelled = findViewById(R.id.chipCancelled);
        chipCompleted = findViewById(R.id.chipCompleted);
    }

    private void setupRecyclerView() {
        adapter = new MyBookingsAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MyBookingsViewModel.class);

        // Observe bookings
        viewModel.getBookingsLiveData().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                allBookings = bookings;
                currentPage = 1; // Reset về trang 1
                updatePaginatedData();
                showBookings();
            } else {
                allBookings.clear();
                showEmptyState();
            }
        });

        // Observe loading state
        viewModel.getLoadingLiveData().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                showLoading();
            }
        });

        // Observe errors
        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                showError(error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe cancel success
        viewModel.getCancelSuccessLiveData().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đã hủy đặt sân thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRetry.setOnClickListener(v -> viewModel.loadUserBookings(currentFilterStatus));

        btnPreviousPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePaginatedData();
                rvBookings.smoothScrollToPosition(0); // Scroll to top
            }
        });

        btnNextPage.setOnClickListener(v -> {
            int totalPages = getTotalPages();
            if (currentPage < totalPages) {
                currentPage++;
                updatePaginatedData();
                rvBookings.smoothScrollToPosition(0); // Scroll to top
            }
        });
    }

    /**
     * Setup filter chips listeners
     */
    private void setupFilterChips() {
        chipAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipAll);
                currentFilterStatus = null;
                viewModel.loadUserBookings(null);
            }
        });

        chipPending.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipPending);
                currentFilterStatus = BookingStatus.PENDING.getValue();
                viewModel.loadUserBookings(currentFilterStatus);
            }
        });

        chipCancelled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipCancelled);
                currentFilterStatus = BookingStatus.CANCELLED.getValue();
                viewModel.loadUserBookings(currentFilterStatus);
            }
        });

        chipCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOtherChips(chipCompleted);
                currentFilterStatus = BookingStatus.COMPLETED.getValue();
                viewModel.loadUserBookings(currentFilterStatus);
            }
        });
    }

    /**
     * Uncheck other chips when one is selected
     */
    private void uncheckOtherChips(Chip selectedChip) {
        if (selectedChip != chipAll) chipAll.setChecked(false);
        if (selectedChip != chipPending) chipPending.setChecked(false);
        if (selectedChip != chipCancelled) chipCancelled.setChecked(false);
        if (selectedChip != chipCompleted) chipCompleted.setChecked(false);
    }

    /**
     * Cập nhật dữ liệu hiển thị theo trang hiện tại
     */
    private void updatePaginatedData() {
        int totalItems = allBookings.size();
        int totalPages = getTotalPages();

        // Tính toán index bắt đầu và kết thúc
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

        // Lấy danh sách booking cho trang hiện tại
        List<GetBookingResponse.BookingData> currentPageBookings =
                allBookings.subList(startIndex, endIndex);

        // Cập nhật adapter
        adapter.setBookings(currentPageBookings);

        // Cập nhật UI pagination
        updatePaginationUI(totalPages, startIndex, endIndex, totalItems);
    }

    /**
     * Cập nhật UI phân trang
     */
    private void updatePaginationUI(int totalPages, int startIndex, int endIndex, int totalItems) {
        // Hiển thị thông tin trang
        tvPageInfo.setText(String.format("Trang %d/%d", currentPage, totalPages));
        tvItemsInfo.setText(String.format("Hiển thị %d-%d / %d",
                startIndex + 1, endIndex, totalItems));

        // Enable/disable nút điều hướng
        btnPreviousPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);

        // Hiển thị pagination nếu có nhiều hơn 1 trang
        if (totalPages > 1) {
            llPagination.setVisibility(View.VISIBLE);
        } else {
            llPagination.setVisibility(View.GONE);
        }
    }

    /**
     * Tính tổng số trang
     */
    private int getTotalPages() {
        int totalItems = allBookings.size();
        return (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvBookings.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.GONE);
        llErrorState.setVisibility(View.GONE);
        llPagination.setVisibility(View.GONE);
    }

    private void showBookings() {
        progressBar.setVisibility(View.GONE);
        rvBookings.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
        llErrorState.setVisibility(View.GONE);
        // llPagination sẽ được hiển thị trong updatePaginationUI
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        rvBookings.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
        llErrorState.setVisibility(View.GONE);
        llPagination.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        rvBookings.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.GONE);
        llErrorState.setVisibility(View.VISIBLE);
        llPagination.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
    }

    @Override
    public void onCancelBooking(GetBookingResponse.BookingData booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc chắn muốn hủy đặt sân này không?")
                .setPositiveButton("Hủy đặt sân", (dialog, which) -> {
                    viewModel.cancelBooking(booking.getId());
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    public void onPaymentBooking(GetBookingResponse.BookingData booking) {
        // Prepare data for payment screen
        String bookingId = booking.getId();
        long totalPrice = (long) booking.getTotalPrice();
        String fieldName = booking.getFieldName();
        String bookingDate = booking.getDate();

        // Get time slots
        ArrayList<String> timeSlotsList = new ArrayList<>();
        if (booking.getTimeSlots() != null) {
            for (com.example.fptstadium.data.model.TimeSlot slot : booking.getTimeSlots()) {
                timeSlotsList.add(slot.getFormattedTimeRange());
            }
        }

        // Navigate to payment screen
        Intent intent = new Intent(this, com.example.fptstadium.ui.payment.PaymentExampleActivity.class);
        intent.putExtra("BOOKING_ID", bookingId);
        intent.putExtra("AMOUNT", totalPrice);
        intent.putExtra("FIELD_NAME", fieldName);
        intent.putExtra("BOOKING_DATE", bookingDate);
        intent.putStringArrayListExtra("TIME_SLOTS", timeSlotsList);
        startActivity(intent);
    }
}
