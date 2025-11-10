package com.example.fptstadium.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fptstadium.R;
import com.example.fptstadium.data.model.TimeSlot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlots = new ArrayList<>();
    private Set<String> bookedSlotIds = new HashSet<>();
    private Set<String> selectedSlotIds = new HashSet<>();
    private OnTimeSlotClickListener listener;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot, boolean isSelected);
    }

    public TimeSlotAdapter(OnTimeSlotClickListener listener) {
        this.listener = listener;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots != null ? timeSlots : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setBookedSlots(Set<String> bookedSlotIds) {
        this.bookedSlotIds = bookedSlotIds != null ? bookedSlotIds : new HashSet<>();
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedSlotIds.clear();
        notifyDataSetChanged();
    }

    public List<TimeSlot> getSelectedTimeSlots() {
        List<TimeSlot> selected = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (selectedSlotIds.contains(slot.getId())) {
                selected.add(slot);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.bind(timeSlot);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView timeText;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.time_slot_card);
            timeText = itemView.findViewById(R.id.time_slot_text);
        }

        public void bind(TimeSlot timeSlot) {
            timeText.setText(timeSlot.getFormattedTimeRange());

            boolean isBooked = bookedSlotIds.contains(timeSlot.getId());
            boolean isSelected = selectedSlotIds.contains(timeSlot.getId());

            // Set background color based on state
            if (isBooked) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.slot_booked));
                cardView.setEnabled(false);
                timeText.setTextColor(itemView.getContext().getColor(R.color.white));
            } else if (isSelected) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.slot_selected));
                cardView.setEnabled(true);
                timeText.setTextColor(itemView.getContext().getColor(R.color.white));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.slot_available));
                cardView.setEnabled(true);
                timeText.setTextColor(itemView.getContext().getColor(R.color.black));
            }

            // Set click listener
            cardView.setOnClickListener(v -> {
                if (!isBooked) {
                    if (isSelected) {
                        selectedSlotIds.remove(timeSlot.getId());
                    } else {
                        selectedSlotIds.add(timeSlot.getId());
                    }
                    notifyItemChanged(getAdapterPosition());

                    if (listener != null) {
                        listener.onTimeSlotClick(timeSlot, !isSelected);
                    }
                }
            });
        }
    }
}

