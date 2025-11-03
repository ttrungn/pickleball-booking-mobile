package com.example.fptstadium.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.Pricing;
import com.example.fptstadium.data.model.TimeSlot;

import java.util.List;
import java.util.Map;

public class PricingAdapter extends RecyclerView.Adapter<PricingAdapter.PricingViewHolder> {

    private List<Pricing> pricingList;
    private Map<String, TimeSlot> timeSlotMap;

    public PricingAdapter(List<Pricing> pricingList, Map<String, TimeSlot> timeSlotMap) {
        this.pricingList = pricingList;
        this.timeSlotMap = timeSlotMap;
    }

    @NonNull
    @Override
    public PricingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pricing, parent, false);
        return new PricingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricingViewHolder holder, int position) {
        Pricing pricing = pricingList.get(position);

        // Set day of week
        holder.tvDayOfWeek.setText(pricing.getDayOfWeekName());

        // Set price
        holder.tvPrice.setText(pricing.getFormattedPrice());

        // Get and display time slot info
        String timeSlotId = pricing.getTimeSlotId();
        if (timeSlotId != null && timeSlotMap.containsKey(timeSlotId)) {
            TimeSlot timeSlot = timeSlotMap.get(timeSlotId);
            if (timeSlot != null) {
                holder.tvTimeSlotId.setText(timeSlot.getFormattedTimeRange());
            } else {
                holder.tvTimeSlotId.setText("Time Slot: " + timeSlotId);
            }
        } else {
            holder.tvTimeSlotId.setText("Đang tải...");
        }
    }

    @Override
    public int getItemCount() {
        return pricingList != null ? pricingList.size() : 0;
    }

    public void updatePricings(List<Pricing> newPricings) {
        this.pricingList = newPricings;
        notifyDataSetChanged();
    }

    static class PricingViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek;
        TextView tvPrice;
        TextView tvTimeSlotId;

        public PricingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTimeSlotId = itemView.findViewById(R.id.tv_time_slot_id);
        }
    }
}

