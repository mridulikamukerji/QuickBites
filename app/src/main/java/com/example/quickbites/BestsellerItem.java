package com.example.quickbites;

import android.graphics.Bitmap;

public class BestsellerItem {
    private String name;
    private Bitmap image;

    // Constructor
    public BestsellerItem(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for image
    public Bitmap getImage() {
        return image;
    }
}
