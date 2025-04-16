package com.example.quickbites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard extends AppCompatActivity {

    private SQLiteDatabase db;
    private ViewPager2 bestsellerViewPager, specialViewPager;
    private List<BestsellerItem> bestsellerItems = new ArrayList<>();
    private List<SpecialItem> specialItems = new ArrayList<>();
    private BestsellerSliderAdapter bestsellerAdapter;
    private SpecialSliderAdapter specialAdapter;

    private MaterialButtonToggleGroup toggleGroup;
    private LinearLayout footerMenu, footerCart, footerOrders;
    private DrawerLayout drawerLayout;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Initialize Views
        bestsellerViewPager = findViewById(R.id.bestsellersSlider);
        specialViewPager = findViewById(R.id.todaysSpecialsSlider);
        toggleGroup = findViewById(R.id.customerCatererToggle);
        footerMenu = findViewById(R.id.footerMenu);
        footerCart = findViewById(R.id.footerCart);
        footerOrders = findViewById(R.id.footerOrders);
        drawerLayout = findViewById(R.id.drawerLayout);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Initialize the database
        db = openOrCreateDatabase("quickbites.db", Context.MODE_PRIVATE, null);

        // Load data dynamically from the database
        loadBestsellers();
        loadSpecials();

        // Setup the adapters for ViewPager2
        bestsellerAdapter = new BestsellerSliderAdapter(this, bestsellerItems);
        specialAdapter = new SpecialSliderAdapter(this, specialItems);

        bestsellerViewPager.setAdapter(bestsellerAdapter);
        specialViewPager.setAdapter(specialAdapter);

        // Link TabLayouts with ViewPagers
        TabLayout bestsellerTabLayout = findViewById(R.id.bestsellersIndicator);
        TabLayout specialsTabLayout = findViewById(R.id.specialsIndicator);

        new TabLayoutMediator(bestsellerTabLayout, bestsellerViewPager,
                (tab, position) -> tab.setText("Item " + (position + 1))).attach();

        new TabLayoutMediator(specialsTabLayout, specialViewPager,
                (tab, position) -> tab.setText("Item " + (position + 1))).attach();

        // Set up toggle button for Dine-in and Takeaway
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btnCustomer) {
                // Handle Dine-in
            } else if (checkedId == R.id.btnCaterer) {
                // Handle Takeaway
            }
        });

        // Footer actions (Menu, Cart, Orders)
        footerMenu.setOnClickListener(v -> {
            // Handle "Menu" button click
        });

        footerCart.setOnClickListener(v -> {
            // Handle "Cart" button click
        });

        footerOrders.setOnClickListener(v -> {
            // Handle "My Orders" button click
        });

        // Side menu (Drawer Layout)
        ImageButton navButton = findViewById(R.id.navButton);
        navButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });
    }

    // Load Bestsellers from the database
    private void loadBestsellers() {
        Cursor cursor = db.rawQuery("SELECT * FROM bestsellers", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                bestsellerItems.add(new BestsellerItem(name, image));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // Load Specials from the database
    private void loadSpecials() {
        Cursor cursor = db.rawQuery("SELECT * FROM specials", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                specialItems.add(new SpecialItem(image, name, 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // Add item to order (Dine-in or Takeaway)
    public void addItemToOrder(String name, String image, int quantity, boolean isTakeaway) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("image", image);
        values.put("quantity", quantity);

        if (isTakeaway) {
            db.insert("takeaway", null, values);
        } else {
            db.insert("dinein", null, values);
        }
    }

    // On button click for increasing item count
    private void onIncreaseButtonClick(boolean isBestseller, int position) {
        if (isBestseller) {
            BestsellerItem item = bestsellerItems.get(position);
            addItemToOrder(item.getName(), item.getImage(), 1, false); // Dine-in by default
        } else {
            SpecialItem item = specialItems.get(position);
            item.setCount(item.getCount() + 1);
            addItemToOrder(item.getName(), item.getImage(), item.getCount(), false); // Dine-in by default
        }
    }

    // On button click for decreasing item count
    private void onDecreaseButtonClick(boolean isBestseller, int position) {
        if (isBestseller) {
            BestsellerItem item = bestsellerItems.get(position);
            addItemToOrder(item.getName(), item.getImage(), -1, false); // Dine-in by default
        } else {
            SpecialItem item = specialItems.get(position);
            if (item.getCount() > 0) {
                item.setCount(item.getCount() - 1);
                addItemToOrder(item.getName(), item.getImage(), item.getCount(), false); // Dine-in by default
            }
        }
    }
}
