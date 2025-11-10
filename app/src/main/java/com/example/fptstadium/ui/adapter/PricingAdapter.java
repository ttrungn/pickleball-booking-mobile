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

        holder.tvDayOfWeek.setText(pricing.getDayOfWeekName());
        holder.tvPrice.setText(pricing.getFormattedPrice());

        String loading = holder.itemView.getContext().getString(R.string.pricing_loading);
        String prefix = holder.itemView.getContext().getString(R.string.pricing_range_prefix);

        // Time slot range (either direct or from map)
        String timeRange = pricing.getTimeSlotStartTime() != null && pricing.getTimeSlotEndTime() != null
                ? formatTime(pricing.getTimeSlotStartTime()) + " - " + formatTime(pricing.getTimeSlotEndTime())
                : pricing.getFormattedTimeRangeOrNull();

        if (timeRange == null) {
            String timeSlotId = pricing.getTimeSlotId();
            if (timeSlotId != null && timeSlotMap.containsKey(timeSlotId)) {
                TimeSlot ts = timeSlotMap.get(timeSlotId);
                timeRange = ts != null ? ts.getFormattedTimeRange() : null;
            }
        }
        holder.tvTimeSlotId.setText(timeRange != null ? timeRange : loading);

        // Pricing batch range (RangeStartTime/RangeEndTime) if exists and different
        if (pricing.getRangeStartTime() != null && pricing.getRangeEndTime() != null) {
            String batchRange = formatTime(pricing.getRangeStartTime()) + " - " + formatTime(pricing.getRangeEndTime());
            if (batchRange.equals(timeRange)) {
                holder.tvPricingRange.setVisibility(View.GONE);
            } else {
                holder.tvPricingRange.setVisibility(View.VISIBLE);
                holder.tvPricingRange.setText(prefix + " " + batchRange);
            }
        } else {
            holder.tvPricingRange.setVisibility(View.GONE);
        }
    }

    private String formatTime(String t) {
        if (t == null) return "";
        if (t.length() > 5 && t.charAt(5) == ':') return t.substring(0,5);
        return t;
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
        TextView tvPricingRange; // new

        public PricingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTimeSlotId = itemView.findViewById(R.id.tv_time_slot_id);
            tvPricingRange = itemView.findViewById(R.id.tv_pricing_range);
        }
    }
}
