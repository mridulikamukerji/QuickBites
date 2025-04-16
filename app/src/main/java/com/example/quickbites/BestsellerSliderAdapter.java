package com.example.quickbites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BestsellerSliderAdapter extends RecyclerView.Adapter<BestsellerSliderAdapter.ViewHolder> {
    private List<BestsellerItem> bestsellerItems;

    // Constructor
    public BestsellerSliderAdapter(List<BestsellerItem> bestsellerItems) {
        this.bestsellerItems = bestsellerItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_slider_bestsellers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BestsellerItem bestsellerItem = bestsellerItems.get(position);

        // Set item name
        holder.nameTextView.setText(bestsellerItem.getName());

        // Set image using Glide for better performance
        Glide.with(holder.imageView.getContext())
                .load(bestsellerItem.getImage())  // Assuming getImage() returns a URL or a file path
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bestsellerItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewBestseller);
            imageView = itemView.findViewById(R.id.imageViewBestseller);
        }
    }
}
