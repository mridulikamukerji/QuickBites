package com.example.quickbites;

import android.graphics.Bitmap;

public class SpecialItem {
    private Bitmap image;
    private String name;
    private int count;

    // Constructor
    public SpecialItem(Bitmap image, String name, int count) {
        this.image = image;
        this.name = name;
        this.count = count;
    }

    // Getter for image
    public Bitmap getImage() {
        return image;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for count
    public int getCount() {
        return count;
    }

    // Setter for count
    public void setCount(int count) {
        this.count = count;
    }
}
