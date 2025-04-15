package com.example.quickbites;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class CustomerDashboard<SliderItem, CustomerSliderAdapter> extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navButton;
    private TextView tvWelcome;

    private ViewPager2 specialsSlider, bestsellersSlider;
    private TabLayout specialsIndicator, bestsellersIndicator;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnDineIn, btnTakeAway;

    private LinearLayout footerMenu, footerCart, footerOrders;

    private DatabaseHelper databaseHelper;

    private boolean isDineIn = true; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navButton = findViewById(R.id.navButton);
        tvWelcome = findViewById(R.id.tvWelcome);
        toggleGroup = findViewById(R.id.customerCatererToggle);
        btnDineIn = findViewById(R.id.btnCustomer);
        btnTakeAway = findViewById(R.id.btnCaterer);
        specialsSlider = findViewById(R.id.todaysSpecialsSlider);
        bestsellersSlider = findViewById(R.id.bestsellersSlider);
        specialsIndicator = findViewById(R.id.specialsIndicator);
        bestsellersIndicator = findViewById(R.id.bestsellersIndicator);

        footerMenu = findViewById(R.id.footerMenu);
        footerCart = findViewById(R.id.footerCart);
        footerOrders = findViewById(R.id.footerOrders);

        databaseHelper = new DatabaseHelper(this);

        // Load user name from session or intent
        String userName = getIntent().getStringExtra("user_name");
        if (userName == null) userName = "Customer";
        tvWelcome.setText("Welcome\n" + userName + "!");

        // Setup sidebar navigation
        navButton.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.END));

        // Load data into sliders
        loadSliderItems();

        // Toggle group functionality
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnCustomer) {
                    isDineIn = true;
                    Toast.makeText(this, "Dine In Mode", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.btnCaterer) {
                    isDineIn = false;
                    Toast.makeText(this, "Take Away Mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Footer navigation
        footerMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerMenu.class);
            startActivity(intent);
        });

        footerCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        footerOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        });
    }

    private void loadSliderItems() {
        // Fetch "Today's Specials" items
        List<SliderItem> specialsItems = databaseHelper.getAllSliderItems("todays_specials");

        CustomerSliderAdapter specialsAdapter = new CustomerSliderAdapter(specialsItems);
        specialsSlider.setAdapter(specialsAdapter);
        new TabLayoutMediator(specialsIndicator, specialsSlider, (tab, position) -> {}).attach();

        // Fetch "Bestsellers" items
        List<SliderItem> bestsellersItems = databaseHelper.getAllSliderItems("bestsellers");

        CustomerSliderAdapter bestsellersAdapter = new CustomerSliderAdapter(bestsellersItems);
        bestsellersSlider.setAdapter(bestsellersAdapter);
        new TabLayoutMediator(bestsellersIndicator, bestsellersSlider, (tab, position) -> {}).attach();
    }

    // Store an item to the appropriate table based on toggle selection
    public void saveItem(SliderItem item) {
        if (isDineIn) {
            databaseHelper.insertItemToDineIn(item);
        } else {
            databaseHelper.insertItemToTakeAway(item);
        }
    }
}

