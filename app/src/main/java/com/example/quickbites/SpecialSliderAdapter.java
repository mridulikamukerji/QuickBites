package com.example.quickbites;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SpecialSliderAdapter extends RecyclerView.Adapter<SpecialSliderAdapter.ViewHolder> {
    private List<SpecialItem> specialItems;

    // Constructor
    public SpecialSliderAdapter(List<SpecialItem> specialItems) {
        this.specialItems = specialItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_slider_specials, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SpecialItem specialItem = specialItems.get(position);

        // Set item name
        holder.nameTextView.setText(specialItem.getName());

        // Set image as Bitmap
        holder.imageView.setImageBitmap(specialItem.getImage());
    }

    @Override
    public int getItemCount() {
        return specialItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewSpecial);
            imageView = itemView.findViewById(R.id.imageViewSpecial);
        }
    }
}
