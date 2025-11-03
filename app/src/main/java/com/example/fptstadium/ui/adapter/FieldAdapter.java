package com.example.fptstadium.ui.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fptstadium.R;
import com.example.fptstadium.data.model.response.GetFieldsResponse;
import com.example.fptstadium.ui.field.FieldDetailActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.FieldViewHolder> {

    private static final String TAG = "FieldAdapter";
    private List<GetFieldsResponse.FieldItem> fieldList;

    public FieldAdapter(List<GetFieldsResponse.FieldItem> fieldList) {
        this.fieldList = fieldList;
        Log.d(TAG, "FieldAdapter created with " + (fieldList != null ? fieldList.size() : 0) + " items");
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stadium_card, parent, false);
        return new FieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        GetFieldsResponse.FieldItem field = fieldList.get(position);
        Log.d(TAG, "onBindViewHolder called for position " + position + ": " + field.getName());

        // Set the basic field information
        holder.stadiumName.setText(field.getName());
        holder.stadiumAddress.setText(field.getAddress());

        // Set price info
        String price = String.format(java.util.Locale.getDefault(), "%,.0f VND/hour", field.getPricePerHour());
        holder.stadiumPrice.setText(price);

        // Set location info
        String location = field.getDistrict() + ", " + field.getCity();
        holder.stadiumLocation.setText(location);

        // Load image using Glide with better error handling
        String imageUrl = field.getBluePrintImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = field.getImageUrl();
        }

        Log.d(TAG, "Loading image for field " + field.getName() + ", URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load and cache the image with primaryColor placeholder
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.color.primaryColor)
                    .error(R.color.primaryColor)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.stadiumImage);
        } else {
            // Set primaryColor background when no image
            holder.stadiumImage.setBackgroundResource(R.color.primaryColor);
        }

        // Setup book button click listener
        holder.bookButton.setOnClickListener(v -> {
            String bookingMessage = "Đang đặt sân " + field.getName();
            Toast.makeText(v.getContext(), bookingMessage, Toast.LENGTH_SHORT).show();
        });

        // Setup item click to open detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FieldDetailActivity.class);
            intent.putExtra(FieldDetailActivity.EXTRA_FIELD_ID, field.getId());
            v.getContext().startActivity(intent);
        });

        // Setup image click to open detail
        holder.stadiumImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FieldDetailActivity.class);
            intent.putExtra(FieldDetailActivity.EXTRA_FIELD_ID, field.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int count = fieldList != null ? fieldList.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public void updateFields(List<GetFieldsResponse.FieldItem> newFields) {
        Log.d(TAG, "updateFields called with " + (newFields != null ? newFields.size() : 0) + " items");

        if (newFields != null && !newFields.isEmpty()) {
            Log.d(TAG, "Updating fieldList. Old size: " + (this.fieldList != null ? this.fieldList.size() : 0) + ", New size: " + newFields.size());
            this.fieldList = newFields;
            notifyDataSetChanged();
            Log.d(TAG, "notifyDataSetChanged() called. Current item count: " + getItemCount());
        } else {
            Log.w(TAG, "Attempted to update with null or empty list!");
        }
    }

    static class FieldViewHolder extends RecyclerView.ViewHolder {
        ImageView stadiumImage;
        TextView stadiumName;
        TextView stadiumLocation;
        TextView stadiumAddress;
        TextView stadiumPrice;
        MaterialButton bookButton;

        public FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            stadiumImage = itemView.findViewById(R.id.stadium_image);
            stadiumName = itemView.findViewById(R.id.stadium_name);
            stadiumLocation = itemView.findViewById(R.id.stadium_location);
            stadiumAddress = itemView.findViewById(R.id.stadium_address);
            stadiumPrice = itemView.findViewById(R.id.stadium_price);
            bookButton = itemView.findViewById(R.id.book_button);
        }
    }
}
