package com.example.fptstadium.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.TimeSlot;
import com.example.fptstadium.data.model.response.GetBookingResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.BookingViewHolder> {

    private List<GetBookingResponse.BookingData> bookings = new ArrayList<>();
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onCancelBooking(GetBookingResponse.BookingData booking);
    }

    public MyBookingsAdapter(OnBookingActionListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<GetBookingResponse.BookingData> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        GetBookingResponse.BookingData booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFieldName;
        private TextView tvStatus;
        private TextView tvBookingDate;
        private LinearLayout llTimeSlots;
        private TextView tvTotalPrice;
        private Button btnCancelBooking;
        private LinearLayout llWarningNotice;


        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvFieldName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            llTimeSlots = itemView.findViewById(R.id.llTimeSlots);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
           llWarningNotice = itemView.findViewById(R.id.llWarningNotice);
        }

        public void bind(GetBookingResponse.BookingData booking) {
            tvFieldName.setText(booking.getFieldName() != null ? booking.getFieldName() : "N/A");

            // Display booking date without emoji
            if (booking.getDate() != null && !booking.getDate().isEmpty()) {
                tvBookingDate.setText("Ngày đặt: " + formatDate(booking.getDate()));
            } else {
                tvBookingDate.setText("Ngày đặt: N/A");
            }

            String status = booking.getStatus();
            tvStatus.setText(getStatusText(status));
            setStatusStyle(tvStatus, status);

            llTimeSlots.removeAllViews();
            if (booking.getTimeSlots() != null && !booking.getTimeSlots().isEmpty()) {
                // Sort time slots before displaying
                List<TimeSlot> sortedTimeSlots = sortTimeSlots(booking.getTimeSlots());
                for (TimeSlot slot : sortedTimeSlots) {
                    addTimeSlotView(slot);
                }
            }

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvTotalPrice.setText(formatter.format(booking.getTotalPrice()) + " VND");

            // Show warning notice and cancel button for Pending status only
            if ("Pending".equalsIgnoreCase(status)) {
                llWarningNotice.setVisibility(View.VISIBLE);
                btnCancelBooking.setVisibility(View.VISIBLE);
                btnCancelBooking.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelBooking(booking);
                    }
                });

            } else if ("Confirmed".equalsIgnoreCase(status)) {
                // Only show Cancel button for Confirmed status
                llWarningNotice.setVisibility(View.GONE);
                btnCancelBooking.setVisibility(View.VISIBLE);
                btnCancelBooking.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelBooking(booking);
                    }
                });
            } else {
                // Hide both buttons for other statuses
                llWarningNotice.setVisibility(View.GONE);
                btnCancelBooking.setVisibility(View.GONE);
            }
        }

        /**
         * Sort time slots by start time
         */
        private List<TimeSlot> sortTimeSlots(List<TimeSlot> timeSlots) {
            List<TimeSlot> sorted = new ArrayList<>(timeSlots);
            Collections.sort(sorted, new Comparator<TimeSlot>() {
                @Override
                public int compare(TimeSlot slot1, TimeSlot slot2) {
                    String start1 = slot1.getStartTime();
                    String start2 = slot2.getStartTime();

                    if (start1 == null && start2 == null) return 0;
                    if (start1 == null) return 1;
                    if (start2 == null) return -1;

                    return start1.compareTo(start2);
                }
            });
            return sorted;
        }

        private void addTimeSlotView(TimeSlot slot) {
            // Create simple text view for time slot (no price)
            TextView tvTime = new TextView(itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 8);
            tvTime.setLayoutParams(params);
            tvTime.setText("• " + slot.getFormattedTimeRange());
            tvTime.setTextSize(14);
            tvTime.setTextColor(Color.parseColor("#424242"));
            tvTime.setPadding(16, 0, 0, 0);

            llTimeSlots.addView(tvTime);
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";
            switch (status.toLowerCase()) {
                case "pending":
                    return "Chờ thanh toán";
                case "confirmed":
                    return "Đã xác nhận";
                case "paid":
                    return "Đã thanh toán";
                case "completed":
                    return "Hoàn thành";
                case "cancelled":
                    return "Đã hủy";
                default:
                    return status;
            }
        }

        private void setStatusStyle(TextView textView, String status) {
            if (status == null) {
                textView.setTextColor(Color.parseColor("#757575"));
                textView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                return;
            }

            switch (status.toLowerCase()) {
                case "pending":
                    textView.setTextColor(Color.parseColor("#F57C00"));
                    textView.setBackgroundColor(Color.parseColor("#FFE0B2"));
                    break;
                case "confirmed":
                    textView.setTextColor(Color.parseColor("#1976D2"));
                    textView.setBackgroundColor(Color.parseColor("#BBDEFB"));
                    break;
                case "paid":
                case "completed":
                    textView.setTextColor(Color.parseColor("#2E7D32"));
                    textView.setBackgroundColor(Color.parseColor("#C8E6C9"));
                    break;
                case "cancelled":
                    textView.setTextColor(Color.parseColor("#C62828"));
                    textView.setBackgroundColor(Color.parseColor("#FFCDD2"));
                    break;
                default:
                    textView.setTextColor(Color.parseColor("#757575"));
                    textView.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    break;
            }
        }

        private String formatDate(String date) {
            // Convert from "2025-11-05" to "05/11/2025"
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
    }
}
