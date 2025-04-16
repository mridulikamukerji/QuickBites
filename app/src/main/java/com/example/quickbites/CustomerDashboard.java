package com.example.quickbites;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.List;

public class CustomerDashboard extends AppCompatActivity {

    private ViewPager2 viewPagerSpecials;
    private SpecialSliderAdapter specialSliderAdapter;

    private ViewPager2 viewPagerBestsellers;
    private BestsellerSliderAdapter bestSellerSliderAdapter;  // Create an adapter for the best sellers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Link with XML ViewPager2 for Today's Specials
        viewPagerSpecials = findViewById(R.id.todaysSpecialsSlider);

        // Link with XML ViewPager2 for Best Sellers
        viewPagerBestsellers = findViewById(R.id.bestsellersSlider);

        // Get data from SQLite for Today's Specials
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<SpecialItem> specials = dbHelper.getAllCustomerSpecials();  // Method that fetches special items for customer
        List<BestsellerItem> bestSellers = dbHelper.getAllBestsellers(); // Method to fetch best sellers from DB

        // Set adapters
        specialSliderAdapter = new SpecialSliderAdapter(specials);
        bestSellerSliderAdapter = new BestsellerSliderAdapter(bestSellers);  // Set adapter for best sellers

        // Set adapter for the Best Sellers ViewPager2
        viewPagerSpecials.setAdapter(specialSliderAdapter);
        viewPagerBestsellers.setAdapter(bestSellerSliderAdapter);  // Set adapter for best sellers
    }
}
