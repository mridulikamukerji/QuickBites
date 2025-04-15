package com.example.quickbites;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class LoginActivity extends AppCompatActivity {

    EditText enterEmail, enterPassword;
    Button btnLogin, btnSignup;
    MaterialButton btnCustomer, btnCaterer;
    MaterialButtonToggleGroup toggleGroup;
    DatabaseHelper dbHelper;

    boolean isCustomer = true; // Default user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterEmail = findViewById(R.id.enterEmail);
        enterPassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnCustomer = findViewById(R.id.btnCustomer);
        btnCaterer = findViewById(R.id.btnCaterer);
        toggleGroup = findViewById(R.id.customerCatererToggle);
        dbHelper = new DatabaseHelper(this);

        // Toggle user type
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnCustomer) {
                    isCustomer = true;
                } else if (checkedId == R.id.btnCaterer) {
                    isCustomer = false;
                }
            }
        });

        // Login button
        btnLogin.setOnClickListener(v -> {
            String email = enterEmail.getText().toString().trim();
            String password = enterPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isCustomer) {
                if (isValidCustomer(email, password)) {
                    Toast.makeText(this, "Customer Login Successful!", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(this, CustomerDashboardActivity.class));
                } else {
                    Toast.makeText(this, "Invalid customer credentials!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isValidCaterer(email, password)) {
                    Toast.makeText(this, "Caterer Login Successful!", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(this, CatererDashboardActivity.class));
                } else {
                    Toast.makeText(this, "Invalid caterer credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign Up button
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Validate customer login
    private boolean isValidCustomer(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CUSTOMERS,
                null,
                DatabaseHelper.COLUMN_CUST_EMAIL + "=? AND " + DatabaseHelper.COLUMN_CUST_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Validate caterer login
    private boolean isValidCaterer(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATERERS,
                null,
                DatabaseHelper.COLUMN_CAT_EMAIL + "=? AND " + DatabaseHelper.COLUMN_CAT_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
}

